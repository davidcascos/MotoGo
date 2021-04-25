package com.dcascos.motogo.constants;

public class Constants {

	//Text formats
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
		Password must contain a length of at least 6 characters.*/
	public static final String PASSWORDFORMAT = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";

	//Google
	public static final int REQUEST_CODE_GOOGLE = 1;

	//System
	public static final int REQUEST_CODE_GALLERY = 2;
	public static final int REQUEST_CODE_PHOTO = 3;
	public static final int REQUEST_CODE_GALLERY_PROFILE = 4;
	public static final int REQUEST_CODE_GALLERY_COVER = 5;
	public static final int REQUEST_CODE_PHOTO_PROFILE = 6;
	public static final int REQUEST_CODE_PHOTO_COVER = 7;

	//Folders
	public static final String FOLDER_IMAGES = "images";
	public static final String FOLDER_COVER = "cover";
	public static final String FOLDER_PROFILE = "profile";

	//User variables
	public static final String USERS = "Users";
	public static final String USER_FULLNAME = "fullName";
	public static final String USER_USERNAME = "username";
	public static final String USER_EMAIL = "email";
	public static final String USER_IMAGECOVER = "imageCover";
	public static final String USER_IMAGEPROFILE = "imageProfile";

	//Post variables
	public static final String POSTS = "Posts";
	public static final String POST_CREATIONDATE = "creationDate";
	public static final String POST_TITLE = "title";
	public static final String POST_LOCATION = "location";
	public static final String POST_DESCRIPTION = "description";
	public static final String POST_IMAGE = "image";
	public static final String POST_USERID = "userId";

	//Comments
	public static final String COMMENTS = "Comments";
	public static final String COMMENT_CREATIONDATE = "creationDate";
	public static final String COMMENT_TEXT = "commentText";
	public static final String COMMENT_USERID = "userId";
	public static final String COMMENT_POSTID = "postId";

	//Comments
	public static final String LIKES = "Likes";
	public static final String LIKE_CREATIONDATE = "creationDate";
	public static final String LIKE_POSTID = "postId";
	public static final String LIKE_USERID = "userId";
}
