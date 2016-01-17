package com.example.gitnb.api;

public class TrendingClient extends RetrofitNetworkAbs{

	private TrendingService trendingService;
	
	private TrendingClient(){
		trendingService = OauthUrlRetrofit.getRetrofit().create(TrendingService.class);
	}
	
    public static TrendingClient getNewInstance() {
        return new TrendingClient();
    }
    
	public void trendingReposList(String language, String since){
		execute(trendingService.trendingReposList(language, since));
	}
    
	public void trendingShowCase(){
		execute(trendingService.trendingShowCase());
	}
	
	public void trendingShowCase(String slug){
		execute(trendingService.trendingShowCase(slug));
	}
	
	@Override
	public TrendingClient setNetworkListener(NetworkListener networkListener) {
        return setNetworkListener(networkListener, this);
	}

}
