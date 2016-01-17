package src.com.example.gitnb.api;

import com.example.gitnb.model.Repository;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 07/08/2014.
 */
public interface RepoActionsService {

    @GET("user/starred/{owner}/{name}")
    Call<Object> checkIfRepoIsStarred(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @PUT("user/starred/{owner}/{name}")
    Call<Object> starRepo(@Path("owner") String owner, @Path("name") String repo);

    @DELETE("/user/starred/{owner}/{name}")
    Call<Object> unstarRepo(@Path("owner") String owner, @Path("name") String repo);

    @GET("user/subscriptions/{owner}/{name}")
    Call<Object> checkIfRepoIsWatched(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @PUT("user/subscriptions/{owner}/{name}")
    Call<Object> watchRepo(@Path("owner") String owner, @Path("name") String repo);

    @DELETE("user/subscriptions/{owner}/{name}")
    Call<Object> unwatchRepo(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @POST("repos/{owner}/{name}/forks")
    Call<Repository> forkRepo(@Path("owner") String owner, @Path("name") String repo);

    @Headers("Content-Length: 0")
    @POST("repos/{owner}/{name}/forks")
    Call<Repository> forkRepo(@Path("owner") String owner, @Path("name") String repo, @Query("organization") String org);
}
