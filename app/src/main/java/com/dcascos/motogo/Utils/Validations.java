package com.dcascos.motogo.Utils;

import android.content.Context;

import com.dcascos.motogo.Constants.Constants;
import com.dcascos.motogo.R;
import com.google.android.material.textfield.TextInputLayout;

public class Validations {

	public static boolean validateIsEmpty(Context context, TextInputLayout textInputLayout) {
		String value = textInputLayout.getEditText().getText().toString().trim();

		if (value.isEmpty()) {
			textInputLayout.setError(context.getString(R.string.fieldCanNotEmpty));
			return false;
		} else {
			textInputLayout.setError(null);
			textInputLayout.setErrorEnabled(false);
			return true;
		}
	}

	public static boolean validateFullNameFormat(Context context, TextInputLayout textInputLayout) {
		String value = textInputLayout.getEditText().getText().toString().trim();

		if (value.isEmpty()) {
			textInputLayout.setError(context.getString(R.string.fieldCanNotEmpty));
			return false;
		} else if (!value.matches(Constants.NAMEFORMAT)) {
			textInputLayout.setError(context.getString(R.string.invalidFormat, context.getString(R.string.fullName)));
			return false;
		} else {
			textInputLayout.setError(null);
			textInputLayout.setErrorEnabled(false);
			return true;
		}
	}

	public static boolean validateUsernameFormat(Context context, TextInputLayout textInputLayout) {
		String value = textInputLayout.getEditText().getText().toString().trim();

		if (value.isEmpty()) {
			textInputLayout.setError(context.getString(R.string.fieldCanNotEmpty));
			return false;
		} else if (value.length() > 15) {
			textInputLayout.setError(context.getString(R.string.usernameToLarge));
			return false;
		} else if (!value.matches(Constants.USERNAMEFORMAT)) {
			textInputLayout.setError(context.getString(R.string.invalidFormat, context.getString(R.string.username)));
			return false;
		} else {
			textInputLayout.setError(null);
			textInputLayout.setErrorEnabled(false);
			return true;
		}
	}

	public static boolean validateEmailFormat(Context context, TextInputLayout textInputLayout) {
		String value = textInputLayout.getEditText().getText().toString().trim();

		if (value.isEmpty()) {
			textInputLayout.setError(context.getString(R.string.fieldCanNotEmpty));
			return false;
		} else if (!value.matches(Constants.EMAILFORMAT)) {
			textInputLayout.setError(context.getString(R.string.invalidFormat, context.getString(R.string.email)));
			return false;
		} else {
			textInputLayout.setError(null);
			textInputLayout.setErrorEnabled(false);
			return true;
		}
	}

	public static boolean validatePasswordFormat(Context context, TextInputLayout textInputLayout) {
		String value = textInputLayout.getEditText().getText().toString().trim();

		if (value.isEmpty()) {
			textInputLayout.setError(context.getString(R.string.fieldCanNotEmpty));
			return false;
		} else if (!value.matches(Constants.PASSWORDFORMAT)) {
			textInputLayout.setError(context.getString(R.string.invalidFormat, context.getString(R.string.password)));
			return false;
		} else {
			textInputLayout.setError(null);
			textInputLayout.setErrorEnabled(false);
			return true;
		}
	}

}
