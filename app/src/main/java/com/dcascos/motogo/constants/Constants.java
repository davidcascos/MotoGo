package com.dcascos.motogo.constants;

public class Constants {

	public static final String NAMEFORMAT = "^[a-zA-Z ]*$";

	/*	Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.
		Username allowed of the dot (.), underscore (_), and hyphen (-).
		The dot (.), underscore (_), or hyphen (-) must not be the first or last character.
		The dot (.), underscore (_), or hyphen (-) does not appear consecutively.
		The number of characters must be between 1 to 15.*/
	public static final String USERNAMEFORMAT = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,13}[a-zA-Z0-9]$";

	public static final String EMAILFORMAT = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

/*	Password must contain at least one digit [0-9].
	Password must contain at least one lowercase Latin character [a-z].
	Password must contain at least one uppercase Latin character [A-Z].
	Password must contain at least one special character like ! @ # & ( ).
	Password must contain a length of at least 8 characters and a maximum of 20 characters.
	*/
	public static final String PASSWORDFORMAT = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

}
