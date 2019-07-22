package com.wuzhizhan.mybatis.contributor;

import com.google.common.base.Optional;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import com.wuzhizhan.mybatis.annotation.Annotation;
import com.wuzhizhan.mybatis.dom.model.IdDomElement;
import com.wuzhizhan.mybatis.util.Icons;
import com.wuzhizhan.mybatis.util.JavaUtils;
import com.wuzhizhan.mybatis.util.MapperUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yanglin
 */
public class TestParamContributor extends CompletionContributor {

  private static final Logger logger = LoggerFactory.getLogger(TestParamContributor.class);

  public TestParamContributor() {
    extend(CompletionType.BASIC,
        XmlPatterns.psiElement()
            .inside(XmlPatterns.xmlAttributeValue()
                .inside(XmlPatterns.xmlAttribute().withName("test"))),
        new CompletionProvider<CompletionParameters>() {
          @Override
          protected void addCompletions(
              @NotNull final CompletionParameters parameters,
              final ProcessingContext context,
              @NotNull final CompletionResultSet result) {
            final PsiElement position = parameters.getPosition();
            addElementForPsiParameter(
                position.getProject(),
                result,
                MapperUtils.findParentIdDomElement(position).orNull());
          }
        });
  }

  static void show(Project project, String content) {
    Notification notification = new NotificationGroup(
        "GradleDependencies",
        NotificationDisplayType.NONE,
        true
    ).createNotification("mybatis", content, NotificationType.INFORMATION, null);
    Notifications.Bus.notify(notification, project);
  }

  static Set<String> IGNORE_TYPES = new HashSet<>();

  static {
    IGNORE_TYPES.add("org.springframework.data.domain.Pageable");
    IGNORE_TYPES.add("org.springframework.data.domain.Sort");
    IGNORE_TYPES.add("org.apache.ibatis.session.RowBounds");
    IGNORE_TYPES.add("com.github.pagehelper.PageRowBounds");
    IGNORE_TYPES.add("com.baomidou.mybatisplus.plugins.pagination.Pagination");
    IGNORE_TYPES.add("com.baomidou.mybatisplus.plugins.Page");
  }

  static void addElementForPsiParameter(
      @NotNull final Project project,
      @NotNull final CompletionResultSet result,
      @Nullable final IdDomElement element) {
    if (element == null) {
      return;
    }

    final PsiMethod method = JavaUtils.findMethod(project, element).orNull();

    if (method == null) {
      logger.info("psiMethod null");
      return;
    }

    final PsiParameter[] parameters = method.getParameterList().getParameters();

    // For a single parameter MyBatis uses its name, while for a multitude they're
    // named as param1, param2, etc. I'll check if the @Param annotation [value] is present
    // and eventually I'll use its text.
    List<PsiParameter> params = Arrays.stream(parameters)
        .filter(p -> !IGNORE_TYPES.contains(p.getType().getCanonicalText(false))).collect(
            Collectors.toList());

    int size = params.size();
    if (size > 0) {
      String canonicalText = params.get(0).getType().getCanonicalText(false);
      if (canonicalText.endsWith(">")) {
        canonicalText = canonicalText.substring(0, canonicalText.indexOf('<'));
      }
      if (size == 1 && isCustClass(canonicalText)) {
        addResult(project, result, canonicalText, "", 0);
      } else {
        int index = 0;
        int total = size;
        for (PsiParameter parameter : params) {
          Optional<String> value = JavaUtils
              .getAnnotationValueText(parameter, Annotation.JPA_PARAM);
          if (!value.isPresent()) {
            value = JavaUtils.getAnnotationValueText(parameter, Annotation.PARAM);
          }
          String qualifiedName = parameter.getType().getCanonicalText(false);
          String content = value.isPresent() ? value.get() : parameter.getName();
          result.addElement(
              buildLookupElementWithIcon(content,
                  qualifiedName, index++));
          if (qualifiedName.endsWith(">")) {
            qualifiedName = qualifiedName.substring(0, qualifiedName.indexOf('<'));
          }
          if (isCustClass(qualifiedName)) {
            total = addResult(project, result, qualifiedName, content, total);
          }
        }
      }
    }
  }

  private static int addResult(@NotNull Project project,
      @NotNull CompletionResultSet result, String qualifiedName, String prefix, int index) {
    PsiClass psiClass = JavaPsiFacade.getInstance(project)
        .findClass(qualifiedName,
            GlobalSearchScope.projectScope(project));
    if (psiClass != null) {
      PsiMethod[] methods = psiClass.getAllMethods();
      int total = index + methods.length;
      for (PsiMethod m : methods) {
        if (m.hasModifier(JvmModifier.PUBLIC)) {
          PsiType returnType = m.getReturnType();
          if (returnType != null) {
            String name = m.getName();
            if (name.startsWith("get") && !name.equals("getClass")) {
              String content = StringUtils.uncapitalize(name.substring(3));
              if (StringUtils.isNotBlank(prefix)) {
                content = prefix + "." + content;
              }
              String canonicalText = returnType.getCanonicalText(false);
              result.addElement(buildLookupElementWithIcon(content, canonicalText, index++));
              if (canonicalText.endsWith(">")) {
                canonicalText = canonicalText.substring(0, canonicalText.indexOf('<'));
              }
              if (isCustClass(canonicalText)) {
                total = addResult(project, result, canonicalText, content, total);
              }
            }
          }
        }
      }
      return total;
    } else {
      return index;
    }
  }

  private static boolean isCustClass(String qualifiedName) {
    try {
      Class<?> type = Class.forName(qualifiedName);
      return (type.getClassLoader() != null || Map.class.isAssignableFrom(type));
    } catch (ClassNotFoundException e) {
      return true;
    }
  }

  private static LookupElement buildLookupElementWithIcon(
      final String parameterName,
      final String parameterType, int index) {
    return PrioritizedLookupElement.withPriority(
        LookupElementBuilder.create(parameterName)
            .withTypeText(parameterType)
            .withIcon(Icons.PARAM_COMPLETION_ICON),
        9999 - index);
  }
}
