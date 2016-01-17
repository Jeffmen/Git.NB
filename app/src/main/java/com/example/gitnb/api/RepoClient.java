package src.com.example.gitnb.api;

public class RepoClient extends RetrofitNetworkAbs{

	private RepoService repoService;
	
	private RepoClient(){
		repoService = ApiRetrofit.getRetrofit().create(RepoService.class);
	}
	
    public static RepoClient getNewInstance() {
        return new RepoClient();
    }
    
    public void get(String path){
		execute(repoService.get(path));
	}	
    
    public void get(String owner, String repo){
		execute(repoService.get(owner, repo));
	}
    
    public void contents(String owner, String repo){
		execute(repoService.contents(owner, repo));
	}    
    
    public void contentsByRef(String owner, String repo, String path, String pref){  	
		if(path == null || path.isEmpty()){
			execute(repoService.contentsByRef(owner, repo, pref));  
    	}
    	else{
    		execute(repoService.contentsByRef(owner, repo, path, pref));  
    	}
	}
    
    public void readme(String owner, String repo){
		execute(repoService.readme(owner, repo));
	}
    
    public void contents(String owner, String repo, String path){
    	if(path == null || path.isEmpty()){
    		execute(repoService.contents(owner, repo));
    	}
    	else{
    		execute(repoService.contents(owner, repo, path));
    	}
	}
    
    public void contributors(String owner, String repo, int page){
		execute(repoService.contributors(owner, repo, page));
	}
    
    public void contributors(String owner, String repo){
		execute(repoService.contributors(owner, repo));
	}
    
    public void stargazers(String owner, String repo){
		execute(repoService.stargazers(owner, repo));
	}
    
    public void stargazers(String owner, String repo, int page){
		execute(repoService.stargazers(owner, repo, page));
	}
    
    public void events(String owner, String repo, int page){
		execute(repoService.events(owner, repo, page));
	}
    
    public void delete(String owner, String repo){
		execute(repoService.delete(owner, repo));
	}
    
	@Override
	public RepoClient setNetworkListener(NetworkListener networkListener) {
        return setNetworkListener(networkListener, this);
	}

}
