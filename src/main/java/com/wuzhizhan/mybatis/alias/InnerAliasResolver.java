package com.wuzhizhan.mybatis.alias;

import com.google.common.base.Optional;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.wuzhizhan.mybatis.util.JavaUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {

  private final Set<AliasDesc> innerAliasDescs;

  public InnerAliasResolver(Project project) {
    super(project);
    Set<AliasDesc> aliasDescs = new HashSet<>();
    addAliasDesc(project, aliasDescs, "java.lang.String", "string");
    addAliasDesc(project, aliasDescs, "java.lang.Byte", "byte");
    addAliasDesc(project, aliasDescs, "java.lang.Long", "long");
    addAliasDesc(project, aliasDescs, "java.lang.Short", "short");
    addAliasDesc(project, aliasDescs, "java.lang.Integer", "int");
    addAliasDesc(project, aliasDescs, "java.lang.Integer", "integer");
    addAliasDesc(project, aliasDescs, "java.lang.Double", "double");
    addAliasDesc(project, aliasDescs, "java.lang.Float", "float");
    addAliasDesc(project, aliasDescs, "java.lang.Boolean", "boolean");
    addAliasDesc(project, aliasDescs, "java.util.Date", "date");
    addAliasDesc(project, aliasDescs, "java.math.BigDecimal", "decimal");
    addAliasDesc(project, aliasDescs, "java.lang.Object", "object");
    addAliasDesc(project, aliasDescs, "java.util.Map", "map");
    addAliasDesc(project, aliasDescs, "java.util.HashMap", "hashmap");
    addAliasDesc(project, aliasDescs, "java.util.List", "list");
    addAliasDesc(project, aliasDescs, "java.util.ArrayList", "arraylist");
    addAliasDesc(project, aliasDescs, "java.util.Collection", "collection");
    addAliasDesc(project, aliasDescs, "java.util.Iterator", "iterator");
    innerAliasDescs = Collections.unmodifiableSet(aliasDescs);
  }

  @NotNull
  private void addAliasDesc(Project project, Set<AliasDesc> aliasDescs, String clazzName,
      String alias) {
    Optional<PsiClass> clazz = JavaUtils.findClazz(project, clazzName);
    if (clazz.isPresent()) {
      aliasDescs.add(AliasDesc.create(clazz.get(), alias));
    }
  }

  @NotNull
  @Override
  public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
    return innerAliasDescs;
  }

}
