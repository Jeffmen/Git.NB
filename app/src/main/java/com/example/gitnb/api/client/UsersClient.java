package com.example.gitnb.api.client;

import com.example.gitnb.api.ApiRetrofit;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.service.UsersService;

public class UsersClient extends RetrofitNetworkAbs {

	private UsersService usersService;
	
	private UsersClient(){
		usersService = ApiRetrofit.getRetrofit().create(UsersService.class);
	}
	
    public static UsersClient getNewInstance() {
        return new UsersClient();
    }
    
	public void me(){
		execute(usersService.me());
	}
    
	public void getSingleUser(String username){
		execute(usersService.getSingleUser(username));
	}
	
	public void followUser(String username){
		execute(usersService.followUser(username));
	}
	
	public void checkFollowing(String username){
		execute(usersService.checkFollowing(username));
	}
	
	public void unFollowUser(String username){
		execute(usersService.unfollowUser(username));
	}
	
	public void following(String username, int page){
		execute(usersService.following(username, page));
	}
	
	public void followers(String username, int page){
		execute(usersService.followers(username, page));
	}
	
	public void userReposList(String sort, int page){
		execute(usersService.userReposList(sort, page));
	}
	
	public void userReposList(String username, String sort, int page){
		execute(usersService.userReposList(username, sort, page));
	}
	
	public void events(String username, int page){
		execute(usersService.events(username, page));
	}
	
	public void createdEvents(String username, int page){
		execute(usersService.createdEvents(username, page));
	}
	
	public void orgsByUser(String username, int page){
		execute(usersService.orgsByUser(username, page));
	}
	
	public void orgs(String username){
		execute(usersService.orgs(username));
	}
	
	public void reposByOrgs(String username, String sort, int page){
		execute(usersService.reposByOrgs(username, sort, page));
	}
	
	public void eventsByOrgs(String username, int page){
		execute(usersService.eventsByOrgs(username, page));
	}
	
	public void members(String username, int page){
		execute(usersService.members(username, page));
	}
	
	@Override
	public UsersClient setNetworkListener(NetworkListener networkListener) {
        return setNetworkListener(networkListener, this);
	}

}
