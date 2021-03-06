/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.server;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;

/**
 * A base class that supports assembling an {@link MockMvcSetup} to build a {@link MockMvc} instance.
 *
 */
public abstract class AbstractMockMvcBuilder {

	private WebApplicationContext applicationContext;

	private List<HandlerMapping> handlerMappings;

	private List<HandlerAdapter> handlerAdapters;

	private List<HandlerExceptionResolver> exceptionResolvers;

	private List<ViewResolver> viewResolvers;

	private RequestToViewNameTranslator viewNameTranslator;

	private LocaleResolver localeResolver;

	public final MockMvc build() {

		applicationContext = initApplicationContext();
		ServletContext servletContext = applicationContext.getServletContext();
		
		Assert.notNull(servletContext, "The WebApplicationContext must be initialized with a ServletContext.");
		
		String name = WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
		servletContext.setAttribute(name, applicationContext);

		handlerMappings = Collections.unmodifiableList(initHandlerMappings());
		handlerAdapters = Collections.unmodifiableList(initHandlerAdapters());
		exceptionResolvers = Collections.unmodifiableList(initHandlerExceptionResolvers());
		viewResolvers = Collections.unmodifiableList(initViewResolvers());
		viewNameTranslator = initViewNameTranslator();
		localeResolver = initLocaleResolver();
		
		return new MockMvc(servletContext, createMvcSetup());
	}

	/**
	 * Returns the WebApplicationContext to use when executing requests. Whether the context contains the
	 * all the application configuration or is just an empty placeholder is up to individual sub-classes.
	 * 
	 * <p>The returned WebApplicationContext must be initialized with a {@link ServletContext} instance.
	 * 
	 */
	protected abstract WebApplicationContext initApplicationContext();

	protected abstract List<? extends HandlerMapping> initHandlerMappings();

	protected abstract List<? extends HandlerAdapter> initHandlerAdapters();

	protected abstract List<? extends HandlerExceptionResolver> initHandlerExceptionResolvers();

	protected abstract List<? extends ViewResolver> initViewResolvers();

	protected abstract RequestToViewNameTranslator initViewNameTranslator();

	protected abstract LocaleResolver initLocaleResolver();

	private MockMvcSetup createMvcSetup() {
		
		return new MockMvcSetup() {

			public List<HandlerMapping> getHandlerMappings() {
				return handlerMappings;
			}

			public List<HandlerAdapter> getHandlerAdapters() {
				return handlerAdapters;
			}

			public List<ViewResolver> getViewResolvers() {
				return viewResolvers;
			}

			public List<HandlerExceptionResolver> getExceptionResolvers() {
				return exceptionResolvers;
			}

			public RequestToViewNameTranslator getViewNameTranslator() {
				return viewNameTranslator;
			}

			public LocaleResolver getLocaleResolver() {
				return localeResolver;
			}
		};
	}
}
