package com.example.gitnb.api.service;

import java.util.List;

import com.example.gitnb.model.Content;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.Release;
import com.example.gitnb.model.RepoRequestDTO;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 17/07/2014.
 */
public interface RepoService {
	
    @GET("/{path}")
    Call<Content> get(@Path("path") String path);

    @GET("/repos/{owner}/{name}")
    Call<Repository> get(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/contents")
    Call<List<Content>> contents(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/contents")
    Call<List<Content>> contentsByRef(@Path("owner") String owner, @Path("name") String repo, @Query("ref") String ref);

    @GET("/repos/{owner}/{name}/readme")
    Call<Content> readme(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/readme")
    Call<Content> readme(@Path("owner") String owner, @Path("name") String repo, @Query("ref") String ref);

    @GET("/repos/{owner}/{name}/contents/{path}")
    Call<List<Content>> contents(@Path("owner") String owner, @Path("name") String repo, @Path("path") String path);

    @GET("/repos/{owner}/{name}/contents/{path}")
    Call<List<Content>> contentsByRef(@Path("owner") String owner, @Path("name") String repo, @Path("path") String path, @Query("ref") String ref);

    @GET("/repos/{owner}/{name}/contributors")
    Call<List<User>> contributors(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/contributors")
    Call<List<User>> contributors(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page);

    @GET("/repos/{owner}/{name}/stargazers")
    Call<List<User>> stargazers(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/stargazers")
    Call<List<User>> stargazers(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page);

    @GET("/repos/{owner}/{name}/collaborators")
    Call<List<User>> collaborators(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/collaborators")
    Call<List<User>> collaborators(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page);

    @GET("/repos/{owner}/{name}/releases")
    Call<List<Release>> releases(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/releases")
    Call<List<Release>> releases(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page);

    @GET("/repos/{owner}/{name}/releases/latest")
    Call<Release> lastRelease(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/releases/{id}")
    Call<Release> release(@Path("owner") String owner, @Path("name") String repo, @Path("id") String id);

    //@GET("/repos/{owner}/{name}/compare/{base}...{head}")
    //CompareCommit compareCommits(@Path("owner") String owner, @Path("name") String repo, @Path("base") String base, @Path("head") String head);

    @DELETE("/repos/{owner}/{name}")
    Call<Object> delete(@Path("owner") String owner, @Path("name") String repo);

    @PATCH("/repos/{owner}/{name}")
    Call<Repository> edit(@Path("owner") String owner, @Path("name") String repo, @Body RepoRequestDTO repoRequestDTO);

    //@GET("/repos/{owner}/{name}/events")
    //List<GithubEvent> events(@Path("owner") String owner, @Path("name") String repo);

    @GET("/repos/{owner}/{name}/events")
    Call<List<Event>> events(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page);

    @GET("/repos/{owner}/{name}/forks")
    Call<List<Repository>> listForks(@Path("owner") String owner, @Path("name") String repo, @Query("sort") String sort);

    @GET("/repos/{owner}/{name}/forks")
    Call<List<Repository>> listForks(@Path("owner") String owner, @Path("name") String repo, @Query("sort") String sort, @Query("page") int page);

    //@GET("/repos/{owner}/{name}/commits/{ref}/status")
    //GithubStatusResponse combinedStatus(@Path("owner") String owner, @Path("name") String repo, @Path("ref") String ref);

    //@GET("/repos/{owner}/{name}/commits/{ref}/status")
    //GithubStatusResponse combinedStatus(@Path("owner") String owner, @Path("name") String repo, @Path("ref") String ref, @Query("page") int page);

    @POST("/user/repos")
    Call<Repository> create(@Body RepoRequestDTO repoRequestDTO);

    //@POST("/user/repos")
    //Observable<Repository> createObs(@Body RepoRequestDTO repoRequestDTO);
}
