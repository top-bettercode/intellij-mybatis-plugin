package com.wuzhizhan.mybatis.alias;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.wuzhizhan.mybatis.util.JavaUtils;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {

  private final Set<AliasDesc> innerAliasDescs = ImmutableSet.of(
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.String").orNull(), "string"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Byte").orNull(), "byte"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Long").orNull(), "long"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Short").orNull(), "short"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer").orNull(), "int"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer").orNull(), "integer"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Double").orNull(), "double"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Float").orNull(), "float"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Boolean").orNull(), "boolean"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.Date").orNull(), "date"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.math.BigDecimal").orNull(), "decimal"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Object").orNull(), "object"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.Map").orNull(), "map"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.HashMap").orNull(), "hashmap"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.List").orNull(), "list"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.ArrayList").orNull(), "arraylist"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.Collection").orNull(), "collection"),
      AliasDesc.create(JavaUtils.findClazz(project, "java.util.Iterator").orNull(), "iterator")
  );

  public InnerAliasResolver(Project project) {
    super(project);
    innerAliasDescs.remove(null);
  }

  @NotNull
  @Override
  public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
    return innerAliasDescs;
  }

}
