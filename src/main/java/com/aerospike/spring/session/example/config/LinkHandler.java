/**
 * Copyright 2012-2017 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.aerospike.spring.session.example.config;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.HttpSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.aerospike.spring.session.example.model.Account;

@Component
public class LinkHandler {
	@SuppressWarnings("unchecked")
	public void setupLinks(HttpServletRequest httpRequest, Model model) throws IOException, ServletException {

		HttpSessionManager sessionManager = (HttpSessionManager) httpRequest
				.getAttribute(HttpSessionManager.class.getName());

		SessionRepository<Session> repo = (SessionRepository<Session>) httpRequest
				.getAttribute(SessionRepository.class.getName());

		String currentSessionAlias = sessionManager.getCurrentSessionAlias(httpRequest);

		Map<String, String> sessionIds = sessionManager.getSessionIds(httpRequest);

		model.addAttribute("username", httpRequest.getRemoteUser());

		String unauthenticatedAlias = null;
		String contextPath = httpRequest.getContextPath();
		List<Account> accounts = new ArrayList<>();
		Account currentAccount = null;
		for (Map.Entry<String, String> entry : sessionIds.entrySet()) {
			String alias = entry.getKey();
			String sessionId = entry.getValue();

			Session session = repo.getSession(sessionId);
			if (session == null) {
				continue;
			}

			SecurityContext context = session
					.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			if (null != context) {
				Principal userPrincipal = context.getAuthentication();
				if (userPrincipal != null) {
					String username = userPrincipal.getName();
					if (username == null) {
						unauthenticatedAlias = alias;
						continue;
					}

					String logoutUrl = sessionManager.encodeURL("./logout", alias);
					String switchAccountUrl = sessionManager.encodeURL("./", alias);
					Account account = new Account(username, logoutUrl, switchAccountUrl);
					if (currentSessionAlias.equals(alias)) {
						currentAccount = account;
					} else {
						accounts.add(account);
					}
				}
			}
		}

		String addAlias = unauthenticatedAlias == null ? // <1>
				sessionManager.getNewSessionAlias(httpRequest) : // <2>
				unauthenticatedAlias; // <3>
		String addAccountUrl = sessionManager.encodeURL(contextPath, addAlias); // <4>

		model.addAttribute("currentAccount", currentAccount);
		model.addAttribute("addAccountUrl", addAccountUrl);
		model.addAttribute("accounts", accounts);
	}
}
