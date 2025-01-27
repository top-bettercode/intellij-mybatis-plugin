package com.wuzhizhan.mybatis.provider;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author yanglin
 */
public abstract class MarkerProviderAdaptor implements LineMarkerProvider {

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
        @NotNull Collection<? super LineMarkerInfo<?>> result) {
    }

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

}
