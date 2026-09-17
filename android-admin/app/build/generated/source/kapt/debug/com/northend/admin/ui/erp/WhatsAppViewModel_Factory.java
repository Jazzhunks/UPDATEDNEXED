package com.northend.admin.ui.erp;

import android.content.Context;
import com.northend.admin.data.repository.AdminRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class WhatsAppViewModel_Factory implements Factory<WhatsAppViewModel> {
  private final Provider<AdminRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public WhatsAppViewModel_Factory(Provider<AdminRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public WhatsAppViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static WhatsAppViewModel_Factory create(Provider<AdminRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    return new WhatsAppViewModel_Factory(repositoryProvider, contextProvider);
  }

  public static WhatsAppViewModel newInstance(AdminRepository repository, Context context) {
    return new WhatsAppViewModel(repository, context);
  }
}
