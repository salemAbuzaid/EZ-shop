package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Users {
    private Integer newId;
    TreeMap<Integer, UserClass> userList;

 /*   public Users(TreeMap<Integer, UserClass> userList) {
        this.userList = userList;
    }*/

    public Users(){
        userList = new TreeMap<>();
    }

    public boolean addUser(UserClass user){
        userList.put(user.getId(), user);
        return true;
    }

    public void setNewId(){
        if(userList.isEmpty()){
            newId = 0;
        }
        else {
            newId = userList.lastKey();
        }
    }

    public Integer getNewId(){ return newId; }

    public boolean removeUser(Integer id){
        if(userList.get(id) == null){
            return false;
        }
        else {
            userList.remove(id);
            return true;
        }
    }

    public User getUserById(Integer id){
        if(userList.containsKey(id)) {
            return userList.get(id);
        }
        else{
            return null;
        }
    }

    public List<User> getUsers(){
        return new ArrayList<>(userList.values());
    }

    public Integer getNewUserID(){
        return ++newId;
    }

    public UserClass getUserByUsername(String username){
        if (!userList.isEmpty()) {
            ArrayList<UserClass> users = new ArrayList<>(userList.values());
            for (UserClass u : users) {
                if (u.getUsername().equals(username)) {
                    return u;
                }
            }
        }
        return null;
    }

   public boolean checkCredentials(String username, String password){
        /* CHECK USERNAME */
       if(getUserByUsername(username) == null){
           return false;
       }
       else if(getUserByUsername(username).getPassword().equals(password)){
           return true;
       }
       else{
           return false;
       }
    }
}
