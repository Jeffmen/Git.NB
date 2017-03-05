package com.example.gitnb.api.service;

import java.util.List;

import com.example.gitnb.model.Email;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.model.Organization;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UsersService {

	@GET("users/{user}")
	Call<User> getSingleUser(@Path("user") String user);

	@GET("user/emails")
	Call<List<Email>> userEmails();


	// Followers
	@GET("user/followers")
	Call<List<User>> followers();

	@GET("users/{username}/followers")
	Call<List<User>> followers(@Path("username") String username);

	@GET("user/followers")
	Call<List<User>> followers(@Query("page") int page);

	@GET("users/{username}/followers")
	Call<List<User>> followers(@Path("username") String username, @Query("page") int page);


	// Following
	@GET("user/following")
	Call<List<User>> following();

	@GET("users/{username}/following")
	Call<List<User>> following(@Path("username") String username);

	@GET("user/following")
	Call<List<User>> following(@Query("page") int page);

	@GET("users/{username}/following")
	Call<List<User>> following(@Path("username") String username, @Query("page") int page);

	@GET("user")
	Call<User> me();


	// FOLLOWING USER
	@GET("user/following/{username}")
	Call<Object> checkFollowing(@Path("username") String username);

    @Headers("Content-Length: 0")
	@PUT("user/following/{username}")
	Call<Object> followUser(@Path("username") String username);

	@DELETE("user/following/{username}")
	Call<Object> unfollowUser(@Path("username") String username);


	//ORGS MEMBERS
	@GET("orgs/{org}/members")
	Call<List<User>> orgMembers(@Path("org") String org);

	@GET("orgs/{org}/members")
	Call<List<User>> orgMembers(@Path("org") String org, @Query("page") int page);


	// User repositories
	@GET("/user/repos?type=owner")
	Call<List<Repository>> userReposList(@Query("sort") String sort);

	@GET("/user/repos?type=owner")
	Call<List<Repository>> userReposList(@Query("sort") String sort, @Query("page") int page);

	@GET("/users/{username}/repos?type=owner")
	Call<List<Repository>> userReposList(@Path("username") String username, @Query("sort") String sort);

	@GET("/users/{username}/repos?type=owner")
	Call<List<Repository>> userReposList(@Path("username") String username, @Query("sort") String sort,  @Query("page") int page);

	@GET("/user/repos?affiliation=organization_member")
	Call<List<Repository>> userReposListFromOrgs(@Query("sort") String sort);

	@GET("/user/repos?affiliation=organization_member")
	Call<List<Repository>> userReposListFromOrgs(@Query("sort") String sort, @Query("page") int page);

	//events
	@GET("/users/{username}/received_events")
	Call<List<Event>> events(@Path("username") String username, @Query("page") int page);
	
	@GET("/users/{username}/events")
	Call<List<Event>> createdEvents(@Path("username") String username, @Query("page") int page);	
	
	//organization
	@GET("/users/{username}/orgs")
	Call<List<Organization>> orgsByUser(@Path("username") String username, @Query("page") int page);
	
	@GET("/orgs/{username}")
	Call<Organization> orgs(@Path("username") String username);

	@GET("/orgs/{orgsname}/repos")
	Call<List<Repository>> reposByOrgs(@Path("orgsname") String orgsname, @Query("sort") String sort, @Query("page") int page);

	@GET("/orgs/{orgsname}/events")
	Call<List<Event>> eventsByOrgs(@Path("orgsname") String orgsname, @Query("page") int page);
	
	@GET("/orgs/{orgsname}/members")
	Call<List<User>> members(@Path("orgsname") String orgsname, @Query("page") int page);

}
