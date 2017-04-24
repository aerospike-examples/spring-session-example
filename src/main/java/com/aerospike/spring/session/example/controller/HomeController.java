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

package com.aerospike.spring.session.example.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aerospike.spring.session.example.config.LinkHandler;

@Controller
public class HomeController {
	@Autowired
	private LinkHandler linkHandler;

	@RequestMapping("/logout")
	String logout(HttpServletRequest r, Model model, HttpSession session) throws Exception {
		if (session != null) {
			session.invalidate();
		}
		this.linkHandler.setupLinks(r, model);
		return "redirect:/";
	}

	@RequestMapping("/")
	String index(HttpServletRequest r, Model model) throws Exception {
		this.linkHandler.setupLinks(r, model);
		return "index";
	}

	@RequestMapping("/link")
	String link(HttpServletRequest r, Model model) throws Exception {
		this.linkHandler.setupLinks(r, model);
		return "link";
	}
}
