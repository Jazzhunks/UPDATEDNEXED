package com.northend.admin.ui.erp;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u0012J\u0006\u0010\u0014\u001a\u00020\u0012J\u000e\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u000bJ\u000e\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u000bJ\u0018\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u001d\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u000bH\u0002J\u0018\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u000b2\b\b\u0002\u0010\u001b\u001a\u00020\u000bJ\b\u0010!\u001a\u00020\u0012H\u0002R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\""}, d2 = {"Lcom/northend/admin/ui/erp/WhatsAppViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/northend/admin/data/repository/AdminRepository;", "context", "Landroid/content/Context;", "(Lcom/northend/admin/data/repository/AdminRepository;Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/northend/admin/ui/erp/WhatsAppUiState;", "lastMessagePreview", "", "lastTopThreadId", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "deselectThread", "", "loadTemplates", "loadThreads", "selectThread", "thread", "Lcom/northend/admin/data/remote/models/WhatsAppThread;", "sendMessage", "text", "sendTemplateMessage", "templateName", "showSystemNotification", "title", "message", "startNewChat", "phone", "startPolling", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class WhatsAppViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.northend.admin.data.repository.AdminRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.northend.admin.ui.erp.WhatsAppUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.northend.admin.ui.erp.WhatsAppUiState> uiState = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastTopThreadId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastMessagePreview;
    
    @javax.inject.Inject()
    public WhatsAppViewModel(@org.jetbrains.annotations.NotNull()
    com.northend.admin.data.repository.AdminRepository repository, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.northend.admin.ui.erp.WhatsAppUiState> getUiState() {
        return null;
    }
    
    private final void startPolling() {
    }
    
    public final void loadTemplates() {
    }
    
    public final void loadThreads() {
    }
    
    public final void selectThread(@org.jetbrains.annotations.NotNull()
    com.northend.admin.data.remote.models.WhatsAppThread thread) {
    }
    
    public final void deselectThread() {
    }
    
    public final void startNewChat(@org.jetbrains.annotations.NotNull()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    java.lang.String templateName) {
    }
    
    public final void sendTemplateMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String templateName) {
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    private final void showSystemNotification(java.lang.String title, java.lang.String message) {
    }
}