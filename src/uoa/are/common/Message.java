// Copyright 2015 Tony (Huansheng) Liu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uoa.are.common;

/**
 * Some response messages.
 * 
 * @author hliu482
 *
 */
public class Message {
    public static final String FAILED = "Request failed";
    public static final String SUCCESSFUL = "Request successful";
    public static final String USER_LOGIN = "User login successfully";
    public static final String USER_LOGOUT = "User logout successfully";
    public static final String USER_NOT_LOGIN = "User did not login";
    public static final String ADMIN_ONLY = "Administrator only";
    public static final String WRONG_USERNAME_PASSWORD = "Wrong username or password";
    public static final String USER_DISABLED = "User has been disabled";
    public static final String RULE_NOT_MATCHED = "Username or password is illegal";
}
