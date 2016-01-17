package src.com.example.gitnb.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.example.gitnb.model.Repository;
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.model.search.ShowCaseSearch;

public interface TrendingService {

	@GET("http://trending.codehub-app.com/v2/trending?")
	Call<List<Repository>> trendingReposList(@Query("language") String language, @Query("since") String since);

	@GET("http://trending.codehub-app.com/v2/showcases")
	Call<List<ShowCase>> trendingShowCase();
	
	@GET("http://trending.codehub-app.com/v2/showcases/{slug}")
	Call<ShowCaseSearch> trendingShowCase(@Path("slug") String slug);
}
