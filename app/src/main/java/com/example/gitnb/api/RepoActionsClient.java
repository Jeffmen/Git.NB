package src.com.example.gitnb.api;

public class RepoActionsClient extends RetrofitNetworkAbs{

	private RepoActionsService repoActionsService;
	
	private RepoActionsClient(){
		repoActionsService = ApiRetrofit.getRetrofit().create(RepoActionsService.class);
	}
	
    public static RepoActionsClient getNewInstance() {
        return new RepoActionsClient();
    }	
    
    public void checkIfRepoIsStarred(String owner, String repo){
		execute(repoActionsService.checkIfRepoIsStarred(owner, repo));
	}
    
    public void starRepo(String owner, String repo){
		execute(repoActionsService.starRepo(owner, repo));
	}    
    
    public void unstarRepo(String owner, String repo){
		execute(repoActionsService.unstarRepo(owner, repo));
	}
	
	@Override
	public RepoActionsClient setNetworkListener(NetworkListener networkListener) {

        return setNetworkListener(networkListener, this);
	}

}
