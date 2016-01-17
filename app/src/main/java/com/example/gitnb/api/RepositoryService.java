package com.example.gitnb.api;

import com.example.gitnb.model.Repository;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 08/07/2014.
 */
public interface RepositoryService {

	// User repositories
	@GET("user/repos?type=owner")
	Call<List<Repository>> userReposList(@Query("sort") String sort);

	@GET("user/repos?type=owner")
	Call<List<Repository>> userReposList(@Query("page") int page, @Query("sort") String sort);

	@GET("users/{username}/repos?type=owner")
	Call<List<Repository>> userReposList(@Path("username") String username, @Query("sort") String sort);

	@GET("users/{username}/repos?type=owner")
	Call<List<Repository>> userReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

	@GET("orgs/{org}/repos?type=all")
	Call<List<Repository>> orgsReposList(@Path("org") String org, @Query("sort") String sort);

	@GET("orgs/{org}/repos?type=all")
	Call<List<Repository>> orgsReposList(@Path("org") String org, @Query("page") int page, @Query("sort") String sort);

	@GET("user/repos?affiliation=organization_member")
	Call<List<Repository>> userReposListFromOrgs(@Query("sort") String sort);

	@GET("user/repos?affiliation=organization_member")
	Call<List<Repository>> userReposListFromOrgs(@Query("page") int page, @Query("sort") String sort);

	// Starred repos
	@GET("user/starred?sort=updated")
	Call<List<Repository>> userStarredReposList(@Query("sort") String sort);

	@GET("user/starred?sort=updated")
	Call<List<Repository>> userStarredReposList(@Query("page") int page, @Query("sort") String sort);

	@GET("users/{username}/starred?sort=updated")
	Call<List<Repository>> userStarredReposList(@Path("username") String username, @Query("sort") String sort);

	@GET("users/{username}/starred?sort=updated")
	Call<List<Repository>> userStarredReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

	// Wathched repos
	@GET("user/subscriptions")
	Call<List<Repository>> userSubscribedReposList(@Query("sort") String sort);

	@GET("user/subscriptions")
	Call<List<Repository>> userSubscribedReposList(@Query("page") int page, @Query("sort") String sort);

	@GET("users/{username}/subscriptions")
	Call<List<Repository>> userSubscribedReposList(@Path("username") String username, @Query("sort") String sort);

	@GET("users/{username}/subscriptions")
	Call<List<Repository>> userSubscribedReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

	// Member
	@GET("user/repos?type=member")
	Call<List<Repository>> userMemberRepos();
	// Member
	@GET("user/repos?type=member")
	Call<List<Repository>> userMemberRepos(@Query("page") int page);
}
