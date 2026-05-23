package com.stickbuzz.ui.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stickbuzz.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;

    public MutableLiveData<String> roleLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public void login(String email, String password) {
        repository.login(email, password, roleLiveData, loadingLiveData, errorLiveData);
    }

    public void register(String name, String email, String password) {
        repository.register(name, email, password, roleLiveData, loadingLiveData);
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}
