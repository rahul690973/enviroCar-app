/* 
 * enviroCar 2013
 * Copyright (C) 2013  
 * Martin Dueren, Jakob Moellers, Gerald Pape, Christopher Stephan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 */
package org.envirocar.app.dao.remote;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.envirocar.app.application.ECApplication;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.dao.UserDAO;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.ResourceConflictException;
import org.envirocar.app.dao.exception.UnauthorizedException;
import org.envirocar.app.dao.exception.UserRetrievalException;
import org.envirocar.app.dao.exception.UserUpdateException;
import org.envirocar.app.model.User;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteUserDAO extends BaseRemoteDAO implements UserDAO, AuthenticatedDAO {

	@Override
	public void updateUser(User user) throws UserUpdateException, UnauthorizedException {
		HttpPut put = new HttpPut(ECApplication.BASE_URL+"/users/"+user.getUsername());
		try {
			put.setEntity(new StringEntity(user.toJson()));
			super.executePayloadRequest(put);
		} catch (UnsupportedEncodingException e) {
			throw new UserUpdateException(e);
		} catch (JSONException e) {
			throw new UserUpdateException(e);
		} catch (NotConnectedException e) {
			throw new UserUpdateException(e);
		} catch (ResourceConflictException e) {
			throw new UserUpdateException(e);
		}
	}

	@Override
	public User getUser(String id) throws UserRetrievalException, UnauthorizedException {
		try {
			JSONObject json = readRemoteResource("/users/"+id);
			return User.fromJson(json);
		} catch (IOException e) {
			throw new UserRetrievalException(e);
		} catch (JSONException e) {
			throw new UserRetrievalException(e);
		} catch (NotConnectedException e) {
			throw new UserRetrievalException(e);
		}
		
	}

	@Override
	public void createUser(User newUser) throws UserUpdateException, ResourceConflictException {
		HttpPost post = new HttpPost(ECApplication.BASE_URL+"/users");
		
		try {
			post.setEntity(new StringEntity(newUser.toJson(true)));
		} catch (UnsupportedEncodingException e) {
			throw new UserUpdateException(e);
		} catch (JSONException e) {
			throw new UserUpdateException(e);
		}
		
		try {
			executePayloadRequest(post);
		} catch (NotConnectedException e) {
			throw new UserUpdateException(e);
		} catch (UnauthorizedException e) {
			throw new UserUpdateException(e);
		}
	}

	@Override
	public void getProfilePicture(User user) throws UserRetrievalException {
		
		
		new fetchImage().execute(user);
	}

}
