package com.example.gitnb.api.rxjava;

import com.example.gitnb.model.CommentRequest;
import com.example.gitnb.model.Content;
import com.example.gitnb.model.Email;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.GithubComment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.IssueRequest;
import com.example.gitnb.model.Notification;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.Release;
import com.example.gitnb.model.RepoRequestDTO;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.model.User;
import com.example.gitnb.model.search.ReposSearch;
import com.example.gitnb.model.search.ShowCaseSearch;
import com.example.gitnb.model.search.UsersSearch;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.http.Url;
import rx.Observable;

/**
 * Created by Bernat on 08/08/2014.
 */
public interface ApiRxJavaService {

    @GET
    Observable<Object> url(@Url() String url);

    //Me
    @GET("user")
    Observable<User> me();


    //notifications
    @GET("notifications")
    Observable<ArrayList<Notification>> getNotifications();

    @GET("notifications/threads/{id}")
    Observable<Notification> getNotification(@Path("id") String id);

    @PATCH("notifications/threads/{id}") Observable<Response<Boolean>> markAsRead(@Path("id") String id);

    //Respository
    @GET("search/repositories")
    Observable<ReposSearch> repos(@Query(value = "q", encoded = true) String query);

    @GET("search/repositories")
    Observable<ReposSearch> reposPaginated(@Query(value = "q", encoded = true) String query,
                                           @Query("page") int page);
    
    @GET("search/repositories")
    Observable<ReposSearch> reposPaginated(@Query(value = "q", encoded = true) String query,
                                           @Query("sort") String sort, @Query("order") String order, @Query("page") int page);


    //User
    @GET("search/users")
    Observable<UsersSearch> users(@Query(value = "q", encoded = true) String query);
    
    @GET("search/users")
    Observable<UsersSearch> usersPaginated(@Query(value = "q", encoded = true) String query,
                                           @Query("page") int page);
    
    @GET("search/users")
    Observable<UsersSearch> usersPaginated(@Query(value = "q", encoded = true) String query,
                                           @Query("sort") String sort, @Query("order") String order, @Query("page") int page);


    //Trendinig
    @GET("http://trending.codehub-app.com/v2/trending?")
    Observable<ArrayList<Repository>> trendingReposList(@Query("language") String language,
                                                        @Query("since") String since);

    @GET("http://trending.codehub-app.com/v2/showcases")
    Observable<ArrayList<ShowCase>> trendingShowCase();

    @GET("http://trending.codehub-app.com/v2/showcases/{slug}")
    Observable<ShowCaseSearch> trendingShowCase(@Path("slug") String slug);


    @GET("users/{user}")
    Observable<User> getSingleUser(@Path("user") String user);

    @GET("user/emails")
    Observable<ArrayList<Email>> userEmails();


    // Followers
    @GET("user/followers")
    Observable<ArrayList<User>> followers();

    @GET("users/{username}/followers")
    Observable<ArrayList<User>> followers(@Path("username") String username);

    @GET("user/followers")
    Observable<ArrayList<User>> followers(@Query("page") int page);

    @GET("users/{username}/followers")
    Observable<ArrayList<User>> followers(@Path("username") String username, @Query("page") int page);


    // Following
    @GET("user/following")
    Observable<ArrayList<User>> following();

    @GET("users/{username}/following")
    Observable<ArrayList<User>> following(@Path("username") String username);

    @GET("user/following")
    Observable<ArrayList<User>> following(@Query("page") int page);

    @GET("users/{username}/following")
    Observable<ArrayList<User>> following(@Path("username") String username, @Query("page") int page);


    // FOLLOWING USER
    @GET("user/following/{username}")
    Observable<Object> checkFollowing(@Path("username") String username);

    @Headers("Content-Length: 0")
    @PUT("user/following/{username}")
    Observable<Object> followUser(@Path("username") String username);

    @DELETE("user/following/{username}")
    Observable<Object> unfollowUser(@Path("username") String username);


    //ORGS MEMBERS
    @GET("orgs/{org}/members")
    Observable<ArrayList<User>> orgMembers(@Path("org") String org);

    @GET("orgs/{org}/members")
    Observable<ArrayList<User>> orgMembers(@Path("org") String org, @Query("page") int page);


    //events
    @GET("users/{username}/received_events")
    Observable<ArrayList<Event>> events(@Path("username") String username, @Query("page") int page);

    @GET("users/{username}/events")
    Observable<ArrayList<Event>> createdEvents(@Path("username") String username, @Query("page") int page);

    //organization
    @GET("users/{username}/orgs")
    Observable<ArrayList<Organization>> orgsByUser(@Path("username") String username, @Query("page") int page);

    @GET("orgs/{username}")
    Observable<Organization> orgs(@Path("username") String username);

    @GET("orgs/{orgsname}/repos")
    Observable<ArrayList<Repository>> reposByOrgs(@Path("orgsname") String orgsname, @Query("sort") String sort,
                                                  @Query("page") int page);

    @GET("orgs/{orgsname}/events")
    Observable<ArrayList<Event>> eventsByOrgs(@Path("orgsname") String orgsname, @Query("page") int page);

    @GET("orgs/{orgsname}/members")
    Observable<ArrayList<User>> members(@Path("orgsname") String orgsname, @Query("page") int page);

    // User repositories
    @GET("user/repos?type=owner")
    Observable<ArrayList<Repository>> userReposList(@Query("sort") String sort);

    @GET("user/repos?type=owner")
    Observable<ArrayList<Repository>> userReposList(@Query("page") int page, @Query("sort") String sort);

    @GET("users/{username}/repos?type=owner")
    Observable<ArrayList<Repository>> userReposList(@Path("username") String username, @Query("sort") String sort);

    @GET("users/{username}/repos?type=owner")
    Observable<ArrayList<Repository>> userReposList(@Path("username") String username, @Query("page") int page,
                                                    @Query("sort") String sort);

    @GET("orgs/{org}/repos?type=all")
    Observable<ArrayList<Repository>> orgsReposList(@Path("org") String org, @Query("sort") String sort);

    @GET("orgs/{org}/repos?type=all")
    Observable<ArrayList<Repository>> orgsReposList(@Path("org") String org, @Query("page") int page,
                                                    @Query("sort") String sort);

    @GET("user/repos?affiliation=organization_member")
    Observable<ArrayList<Repository>> userReposListFromOrgs(@Query("sort") String sort);

    @GET("user/repos?affiliation=organization_member")
    Observable<ArrayList<Repository>> userReposListFromOrgs(@Query("page") int page, @Query("sort") String sort);

    // Starred repos
    @GET("user/starred?sort=updated")
    Observable<ArrayList<Repository>> userStarredReposList(@Query("sort") String sort);

    @GET("user/starred?sort=updated")
    Observable<ArrayList<Repository>> userStarredReposList(@Query("page") int page, @Query("sort") String sort);

    @GET("users/{username}/starred?sort=updated")
    Observable<ArrayList<Repository>> userStarredReposList(@Path("username") String username,
                                                           @Query("sort") String sort);

    @GET("users/{username}/starred?sort=updated")
    Observable<ArrayList<Repository>> userStarredReposList(@Path("username") String username,
                                                           @Query("page") int page, @Query("sort") String sort);

    // Wathched repos
    @GET("user/subscriptions")
    Observable<ArrayList<Repository>> userSubscribedReposList(@Query("sort") String sort);

    @GET("user/subscriptions")
    Observable<ArrayList<Repository>> userSubscribedReposList(@Query("page") int page, @Query("sort") String sort);

    @GET("users/{username}/subscriptions")
    Observable<ArrayList<Repository>> userSubscribedReposList(@Path("username") String username,
                                                              @Query("sort") String sort);

    @GET("users/{username}/subscriptions")
    Observable<ArrayList<Repository>> userSubscribedReposList(@Path("username") String username,
                                                              @Query("page") int page, @Query("sort") String sort);

    // Member
    @GET("user/repos?type=member")
    Observable<ArrayList<Repository>> userMemberRepos();

    // Member
    @GET("user/repos?type=member")
    Observable<ArrayList<Repository>> userMemberRepos(@Query("page") int page);


    @GET("{path}")
    Observable<Content> get(@Path("path") String path);

    @GET("repos/{owner}/{name}")
    Observable<Repository> get(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/contents")
    Observable<ArrayList<Content>> contents(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/contents")
    Observable<ArrayList<Content>> contentsByRef(@Path("owner") String owner, @Path("name") String repo,
                                                 @Query("ref") String ref);

    @GET("repos/{owner}/{name}/readme")
    Observable<Content> readme(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/readme")
    Observable<Content> readme(@Path("owner") String owner, @Path("name") String repo, @Query("ref") String ref);

    @GET("repos/{owner}/{name}/contents/{path}")
    Observable<ArrayList<Content>> contents(@Path("owner") String owner, @Path("name") String repo,
                                            @Path("path") String path);

    @GET("repos/{owner}/{name}/contents/{path}")
    Observable<ArrayList<Content>> contentsByRef(@Path("owner") String owner, @Path("name") String repo,
                                                 @Path("path") String path, @Query("ref") String ref);

    @GET("repos/{owner}/{name}/contributors")
    Observable<ArrayList<User>> contributors(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/contributors")
    Observable<ArrayList<User>> contributors(@Path("owner") String owner, @Path("name") String repo,
                                             @Query("page") int page);

    @GET("repos/{owner}/{name}/stargazers")
    Observable<ArrayList<User>> stargazers(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/stargazers")
    Observable<ArrayList<User>> stargazers(@Path("owner") String owner, @Path("name") String repo,
                                           @Query("page") int page);

    @GET("repos/{owner}/{name}/collaborators")
    Observable<ArrayList<User>> collaborators(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/collaborators")
    Observable<ArrayList<User>> collaborators(@Path("owner") String owner, @Path("name") String repo,
                                              @Query("page") int page);

    @GET("repos/{owner}/{name}/releases")
    Observable<ArrayList<Release>> releases(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/releases")
    Observable<ArrayList<Release>> releases(@Path("owner") String owner, @Path("name") String repo,
                                            @Query("page") int page);

    @GET("repos/{owner}/{name}/releases/latest")
    Observable<Release> lastRelease(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/releases/{id}")
    Observable<Release> release(@Path("owner") String owner, @Path("name") String repo, @Path("id") String id);

    //@GET("repos/{owner}/{name}/compare/{base}...{head}")
    //CompareCommit compareCommits(@Path("owner") String owner, @Path("name") String repo, @Path("base") String base, @Path("head") String head);

    @DELETE("repos/{owner}/{name}")
    Observable<Object> delete(@Path("owner") String owner, @Path("name") String repo);

    @PATCH("repos/{owner}/{name}")
    Observable<Repository> edit(@Path("owner") String owner, @Path("name") String repo,
                                @Body RepoRequestDTO repoRequestDTO);

    //@GET("repos/{owner}/{name}/events")
    //List<GithubEvent> events(@Path("owner") String owner, @Path("name") String repo);

    @GET("repos/{owner}/{name}/events")
    Observable<ArrayList<Event>> events(@Path("owner") String owner, @Path("name") String repo,
                                        @Query("page") int page);

    @GET("repos/{owner}/{name}/forks")
    Observable<ArrayList<Repository>> listForks(@Path("owner") String owner, @Path("name") String repo,
                                                @Query("sort") String sort);

    @GET("repos/{owner}/{name}/forks")
    Observable<ArrayList<Repository>> listForks(@Path("owner") String owner, @Path("name") String repo,
                                                @Query("sort") String sort, @Query("page") int page);

    //@GET("repos/{owner}/{name}/commits/{ref}/status")
    //GithubStatusResponse combinedStatus(@Path("owner") String owner, @Path("name") String repo, @Path("ref") String ref);

    //@GET("repos/{owner}/{name}/commits/{ref}/status")
    //GithubStatusResponse combinedStatus(@Path("owner") String owner, @Path("name") String repo, @Path("ref") String ref, @Query("page") int page);

    @POST("user/repos")
    Observable<Repository> create(@Body RepoRequestDTO repoRequestDTO);

    //@POST("user/repos")
    //Observable<Repository> createObs(@Body RepoRequestDTO repoRequestDTO);

    @GET("user/starred/{owner}/{name}")
    Observable<Object> checkIfRepoIsStarred(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @PUT("user/starred/{owner}/{name}")
    Observable<Object> starRepo(@Path("owner") String owner, @Path("name") String repo);

    @DELETE("user/starred/{owner}/{name}")
    Observable<Object> unstarRepo(@Path("owner") String owner, @Path("name") String repo);

    @GET("user/subscriptions/{owner}/{name}")
    Observable<Object> checkIfRepoIsWatched(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @PUT("user/subscriptions/{owner}/{name}")
    Observable<Object> watchRepo(@Path("owner") String owner, @Path("name") String repo);

    @DELETE("user/subscriptions/{owner}/{name}")
    Observable<Object> unwatchRepo(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @POST("repos/{owner}/{name}/forks")
    Observable<Repository> forkRepo(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @POST("repos/{owner}/{name}/forks")
    Observable<Repository> forkRepo(@Path("owner") String owner, @Path("name") String repo,
                                    @Query("organization") String org);


    //Issue
    @GET("issues")
    Observable<ArrayList<Issue>> allIssues(@QueryMap Map<String, String> filter, @Query("page") int page);

    @GET("user/issues")
    Observable<ArrayList<Issue>> myIssues(@QueryMap Map<String, String> filter, @Query("page") int page);

    @GET("repos/{owner}/{name}/issues?sort=updated")
    Observable<ArrayList<Issue>> reposIssues(@Path("owner") String owner, @Path("name") String repo,
                                             @QueryMap Map<String, String> filter);

    @GET("repos/{owner}/{name}/issues?sort=updated")
    Observable<ArrayList<Issue>> reposIssues(@Path("owner") String owner, @Path("name") String repo,
                                             @QueryMap Map<String, String> filter, @Query("page") int page);

    @POST("repos/{owner}/{name}/issues")
    Observable<Issue> createIssue(@Path("owner") String owner, @Path("name") String repo, @Body IssueRequest issue);

    @GET("repos/{owner}/{name}/issues/{num}")
    Observable<Issue> detailIssue(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num);


    @PATCH("/repos/{owner}/{name}/issues/{num}")
    Observable<Issue> closeIssue(@Path("owner") String owner, @Path("name") String repo,
                                 @Path("num") int num, @Body IssueRequest issueRequest);

    @GET("/repos/{owner}/{name}/issues/{num}/comments")
    Observable<ArrayList<GithubComment>> comments(@Path("owner") String owner, @Path("name") String repo,
                                                  @Path("num") int num, @Query("page") int page);

    @POST("/repos/{owner}/{name}/issues/{num}/comments")
    Observable<GithubComment> addComment(@Path("owner") String owner, @Path("name") String repo,
                                         @Path("num") int num, @Body GithubComment comment);

    @PATCH("/repos/{owner}/{name}/issues/{number}")
    Observable<Issue> editIssue(@Path("owner") String owner, @Path("name") String repo,
                                @Path("number") int number, @Body IssueRequest editIssueRequestDTO);

    @DELETE("/repos/{owner}/{name}/issues/comments/{id}")
    Observable<Response<GithubComment>> deleteComment(@Path("owner") String owner, @Path("name") String name,
                                       @Path("id") String id);

    @PATCH("/repos/{owner}/{name}/issues/comments/{id}")
    Observable<GithubComment> editComment(@Path("owner") String owner, @Path("name") String name,
                                          @Path("id") String id, @Body CommentRequest body);
}
