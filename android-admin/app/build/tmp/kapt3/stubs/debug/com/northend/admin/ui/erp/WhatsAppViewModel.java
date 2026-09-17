package com.northend.admin.ui.erp;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rJ\u0006\u0010\u000f\u001a\u00020\rJ\u000e\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0015J\u0018\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u00152\b\b\u0002\u0010\u0017\u001a\u00020\u0015R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u001a"}, d2 = {"Lcom/northend/admin/ui/erp/WhatsAppViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/northend/admin/data/repository/AdminRepository;", "(Lcom/northend/admin/data/repository/AdminRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/northend/admin/ui/erp/WhatsAppUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "deselectThread", "", "loadTemplates", "loadThreads", "selectThread", "thread", "Lcom/northend/admin/data/remote/models/WhatsAppThread;", "sendMessage", "text", "", "sendTemplateMessage", "templateName", "startNewChat", "phone", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class WhatsAppViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.northend.admin.data.repository.AdminRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.northend.admin.ui.erp.WhatsAppUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.northend.admin.ui.erp.WhatsAppUiState> uiState = null;
    
    @javax.inject.Inject()
    public WhatsAppViewModel(@org.jetbrains.annotations.NotNull()
    com.northend.admin.data.repository.AdminRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.northend.admin.ui.erp.WhatsAppUiState> getUiState() {
        return null;
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
}