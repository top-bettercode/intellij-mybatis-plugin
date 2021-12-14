package com.wuzhizhan.mybatis.setting;

import static com.wuzhizhan.mybatis.generate.StatementGenerator.DELETE_GENERATOR;
import static com.wuzhizhan.mybatis.generate.StatementGenerator.INSERT_GENERATOR;
import static com.wuzhizhan.mybatis.generate.StatementGenerator.SELECT_GENERATOR;
import static com.wuzhizhan.mybatis.generate.StatementGenerator.UPDATE_GENERATOR;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.wuzhizhan.mybatis.generate.GenerateModel;
import com.wuzhizhan.mybatis.generate.StatementGenerator;
import java.lang.reflect.Type;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
@State(
    name = "MybatisSettings",
    storages = @Storage("$APP_CONFIG$/mybatis.xml"))
public class MybatisSetting implements PersistentStateComponent<Element> {

  private GenerateModel statementGenerateModel;

  private Set<String> ignoreParamTypes = ImmutableSet
      .of("org.springframework.data.domain.Pageable",
          "org.springframework.data.domain.Sort",
          "org.apache.ibatis.session.RowBounds",
          "com.github.pagehelper.PageRowBounds",
          "com.baomidou.mybatisplus.plugins.pagination.Pagination",
          "com.baomidou.mybatisplus.plugins.Page");

  private Gson gson = new Gson();

  private Type gsonTypeToken = new TypeToken<Set<String>>() {
  }.getType();

  public MybatisSetting() {
    statementGenerateModel = GenerateModel.START_WITH_MODEL;
  }

  public static MybatisSetting getInstance() {
    return ServiceManager.getService(MybatisSetting.class);
  }

  @Nullable
  @Override
  public Element getState() {
    Element element = new Element("MybatisSettings");
    element.setAttribute(INSERT_GENERATOR.getId(), gson.toJson(INSERT_GENERATOR.getPatterns()));
    element.setAttribute(DELETE_GENERATOR.getId(), gson.toJson(DELETE_GENERATOR.getPatterns()));
    element.setAttribute(UPDATE_GENERATOR.getId(), gson.toJson(UPDATE_GENERATOR.getPatterns()));
    element.setAttribute(SELECT_GENERATOR.getId(), gson.toJson(SELECT_GENERATOR.getPatterns()));
    element.setAttribute("statementGenerateModel",
        String.valueOf(statementGenerateModel.getIdentifier()));
    element.setAttribute("ignoreParamTypes", gson.toJson(ignoreParamTypes));
    return element;
  }

  @Override
  public void loadState(Element state) {
    loadState(state, INSERT_GENERATOR);
    loadState(state, DELETE_GENERATOR);
    loadState(state, UPDATE_GENERATOR);
    loadState(state, SELECT_GENERATOR);
    String attribute = state.getAttributeValue("ignoreParamTypes");
    if (StringUtils.isNotBlank(attribute)) {
      ignoreParamTypes = gson.fromJson(attribute, gsonTypeToken);
    }
    statementGenerateModel = GenerateModel
        .getInstance(state.getAttributeValue("statementGenerateModel"));
  }

  private void loadState(Element state, StatementGenerator generator) {
    String attribute = state.getAttributeValue(generator.getId());
    if (null != attribute) {
      generator.setPatterns(gson.fromJson(attribute, gsonTypeToken));
    }
  }

  public Set<String> getIgnoreParamTypes() {
    return ignoreParamTypes;
  }

  public void setIgnoreParamTypes(Set<String> ignoreParamTypes) {
    this.ignoreParamTypes = ignoreParamTypes;
  }

  public GenerateModel getStatementGenerateModel() {
    return statementGenerateModel;
  }

  public void setStatementGenerateModel(GenerateModel statementGenerateModel) {
    this.statementGenerateModel = statementGenerateModel;
  }
}
