/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser.ui.widgets.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.databinding.OptionsEditLoginBinding;
import org.mozilla.vrbrowser.ui.widgets.WidgetManagerDelegate;
import org.mozilla.vrbrowser.ui.widgets.WidgetPlacement;

import mozilla.components.concept.storage.Login;

@SuppressLint("ViewConstructor")
class LoginEditOptionsView extends SettingsView {

    private OptionsEditLoginBinding mBinding;
    private Login mLogin;

    public LoginEditOptionsView(@NonNull Context aContext, @NonNull WidgetManagerDelegate aWidgetManager, @NonNull Login login) {
        super(aContext, aWidgetManager);
        mLogin = login;
        initialize(aContext);
    }

    private void initialize(Context aContext) {
        updateUI();
    }

    @Override
    protected void updateUI() {
        super.updateUI();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Inflate this data binding layout
        mBinding = DataBindingUtil.inflate(inflater, R.layout.options_edit_login, this, true);

        mScrollbar = mBinding.scrollbar;

        // Header
        mBinding.headerLayout.setBackClickListener(view -> {
            onDismiss();
        });

        // Footer
        mBinding.footerLayout.setFooterButtonClickListener(mDelete);

        // Options
        mBinding.siteButton.setOnClickListener(mSiteButtonListener);

        mBinding.usernameEdit.setHint1(getContext().getString(R.string.username_hint));
        mBinding.usernameEdit.setFirstText(mLogin.getUsername());
        mBinding.usernameEdit.setOnClickListener(mUsernameListener);
        setUsername(mLogin.getUsername());

        mBinding.passwordEdit.setHint1(getContext().getString(R.string.password_hint));
        mBinding.passwordEdit.setFirstText(mLogin.getPassword());
        mBinding.passwordEdit.setPasswordToggleVisibility(View.VISIBLE);
        mBinding.passwordEdit.setOnClickListener(mPasswordListener);
        setPassword(mLogin.getPassword());
    }

    @Override
    public void onShown() {
        super.onShown();
        mBinding.headerLayout.setTitle(mLogin.getOrigin());
        mBinding.originText.setText(mLogin.getOrigin());
    }

    @Override
    public void onHidden() {
        if (!isEditing()) {
            super.onHidden();
        }
    }

    @Override
    protected void onDismiss() {
        if (!isEditing()) {
            super.onDismiss();
        }
    }

    @Override
    public boolean isEditing() {
        boolean editing = false;

        if (mBinding.usernameEdit.isEditing()) {
            editing = true;
            mBinding.usernameEdit.cancel();
        }

        if (mBinding.passwordEdit.isEditing()) {
            editing = true;
            mBinding.passwordEdit.cancel();
        }

        return editing;
    }

    private OnClickListener mSiteButtonListener = (view) -> {
        mDelegate.exitWholeSettings();
        mWidgetManager.openNewTabForeground(mLogin.getOrigin());
    };

    private OnClickListener mUsernameListener = (view) -> {
        setUsername(mBinding.usernameEdit.getFirstText());
    };

    private OnClickListener mPasswordListener = (view) -> {
        setPassword(mBinding.passwordEdit.getFirstText());
    };

    private OnClickListener mDelete = (view) -> {
        mWidgetManager.getServicesProvider().getLoginStorage().delete(mLogin);
        onDismiss();
    };

    private void setUsername(String username) {
        mBinding.usernameEdit.setOnClickListener(null);
        mBinding.usernameEdit.setFirstText(username);
        mBinding.usernameEdit.setOnClickListener(mUsernameListener);
        final Login newLogin = mLogin.copy(
                mLogin.getGuid(),
                mLogin.getOrigin(),
                mLogin.getFormActionOrigin(),
                mLogin.getHttpRealm(),
                username,
                mLogin.getPassword(),
                mLogin.getTimesUsed(),
                mLogin.getTimeCreated(),
                mLogin.getTimeLastUsed(),
                mLogin.getTimePasswordChanged(),
                mLogin.getUsernameField(),
                mLogin.getPasswordField()
        );
        mWidgetManager.getServicesProvider().getLoginStorage().update(newLogin);
        mBinding.usernameEdit.setFirstText(username);
    }

    private void setPassword(String password) {
        mBinding.passwordEdit.setOnClickListener(null);
        mBinding.passwordEdit.setFirstText(password);
        mBinding.passwordEdit.setOnClickListener(mPasswordListener);
        final Login newLogin = mLogin.copy(
                mLogin.getGuid(),
                mLogin.getOrigin(),
                mLogin.getFormActionOrigin(),
                mLogin.getHttpRealm(),
                mLogin.getUsername(),
                password,
                mLogin.getTimesUsed(),
                mLogin.getTimeCreated(),
                mLogin.getTimeLastUsed(),
                mLogin.getTimePasswordChanged(),
                mLogin.getUsernameField(),
                mLogin.getPasswordField()
        );
        mWidgetManager.getServicesProvider().getLoginStorage().update(newLogin);
        mBinding.passwordEdit.setFirstText(password);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (oldFocus != null) {
            if (mBinding.usernameEdit.contains(oldFocus) && mBinding.usernameEdit.isEditing()) {
                mBinding.usernameEdit.cancel();
            }
            if (mBinding.passwordEdit.contains(oldFocus) && mBinding.passwordEdit.isEditing()) {
                mBinding.passwordEdit.cancel();
            }
        }
    }

    @Override
    public Point getDimensions() {
        return new Point( WidgetPlacement.dpDimension(getContext(), R.dimen.settings_dialog_width),
                WidgetPlacement.dpDimension(getContext(), R.dimen.display_options_height));
    }

    @Override
    protected SettingViewType getType() {
        return SettingViewType.LOGIN_EDIT;
    }

}
