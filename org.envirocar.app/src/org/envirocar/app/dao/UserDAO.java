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
package org.envirocar.app.dao;

import org.envirocar.app.dao.exception.FriendsRetrievalException;
import org.envirocar.app.dao.exception.ResourceConflictException;
import org.envirocar.app.dao.exception.UnauthorizedException;
import org.envirocar.app.dao.exception.UserRetrievalException;
import org.envirocar.app.dao.exception.UserUpdateException;
import org.envirocar.app.model.User;

public interface UserDAO {

	void updateUser(User user) throws UserUpdateException, UnauthorizedException;

	User getUser(String id) throws UserRetrievalException, UnauthorizedException;

	void createUser(User newUser) throws UserUpdateException, ResourceConflictException;
	
	void getProfilePicture (User user) throws UserRetrievalException;
	
	void getFriends (User user) throws FriendsRetrievalException;
}
