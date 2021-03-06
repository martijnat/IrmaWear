/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.cardemu.httpclient;

import android.os.AsyncTask;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.irmacard.credentials.info.DescriptionStore;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Convenience class to (a)synchroniously do HTTP GET and PUT requests,
 * and serialize the in- and output automatically using Gson. <br/>
 * NOTE: the synchronious methods of this class must not be used on the main thread,
 * as otherwise a NetworkOnMainThreadException will occur.
 */
public class JsonHttpClient {
	private Gson gson;
	private int timeout = 5000;
	private HttpRequestFactory requestFactory;

	/**
	 * Instantiate a new JsonHttpClient.
	 *
	 * @param gson The Gson object that will handle (de)serialization.
	 */
	public JsonHttpClient(Gson gson) {
		this(gson, null);
	}

	/**
	 * Instantiate a new JsonHttpClient.
	 * @param gson The Gson object that will handle (de)serialization.
	 * @param socketFactory The SSLSocketFactory to use.
	 */
	public JsonHttpClient(Gson gson, SSLSocketFactory socketFactory) {
		this.gson = gson;

		requestFactory = new NetHttpTransport.Builder()
				.setSslSocketFactory(socketFactory)
				.build()
				.createRequestFactory(new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest httpRequest) throws IOException {
						httpRequest.setConnectTimeout(timeout);
					}
				});
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Performs a GET on the specified url. See the javadoc of doPost.
	 *
	 * @param type The type to which the return value should be cast. If the casting fails
	 *             an exception will be raised.
	 * @param url The url to post to.
	 * @param <T> The object to post. May be null, in which case we do a GET instead
	 *            of post.
	 * @return The T returned by the server, if successful.
	 * @throws HttpClientException
	 */
	public <T> T doGet(final Type type, String url) throws HttpClientException {
		return doRequest(type, url, null, "", "GET");
	}

	/**
	 * POSTs the specified object to the specified url, and attempts to cast the response
	 * of the server to the type specified by T and type. (Note: apparently it is not possible
	 * get a Type from T or vice versa, so the type variable T and Type type must both be
	 * given. Fortunately, the T is inferrable so this method can be called like so:<br/>
	 * <code>YourType x = doPost(YourType.class, "http://www.example.com/post", object);</code><br/><br/>
	 *
	 * This method does not (yet) support the posting of generics to the server.
	 *
	 * @param type The type to which the return value should be cast. If the casting fails
	 *             an exception will be raised.
	 * @param url The url to post to.
	 * @param object The object to post. May be null, in which case we do a GET instead
	 *               of post.
	 * @param <T> The type to which the return value should be cast.
	 * @return The T returned by the server, if successful.
	 * @throws HttpClientException If the casting failed, <code>status</code> will be zero.
	 * Otherwise, if the communication with the server failed, <code>status</code> will
	 * be an HTTP status code.
	 */
	public <T> T doPost(final Type type, String url, Object object) throws HttpClientException {
		return doRequest(type, url, object, "", "POST");
	}

	/**
	 * POSTs the specified object to the specified url, ignoring the output of the server.<br/><br/>
	 *
	 * This method does not (yet) support the posting of generics to the server.
	 *
	 * @param url The url to post to.
	 * @param object The object to post. May be null, in which case we do a GET instead
	 *               of post.
	 * @throws HttpClientException If the casting failed, <code>status</code> will be zero.
	 * Otherwise, if the communication with the server failed, <code>status</code> will
	 * be an HTTP status code.
	 */
	public void doPost(String url, Object object) throws HttpClientException {
		doRequest(Object.class, url, object, "", "POST");
	}

	/**
	 * POSTs the specified object to the specified url, and attempts to cast the response
	 * of the server to the type specified by T and type. (Note: apparently it is not possible
	 * get a Type from T or vice versa, so the type variable T and Type type must both be
	 * given. Fortunately, the T is inferrable so this method can be called like so:<br/>
	 * <code>YourType x = doPost(YourType.class, "http://www.example.com/post", object);</code><br/><br/>
	 *
	 * This method does not (yet) support the posting of generics to the server.
	 *
	 * @param type The type to which the return value should be cast. If the casting fails
	 *             an exception will be raised.
	 * @param url The url to post to.
	 * @param object The object to post. May be null, in which case we do a GET instead
	 *               of post.
	 * @param authorization The authorization header to be sent. If this equals the empty string, no such header will
	 *                      be sent.
	 * @param <T> The type to which the return value should be cast.
	 * @return The T returned by the server, if successful.
	 * @throws HttpClientException If the casting failed, <code>status</code> will be zero.
	 * Otherwise, if the communication with the server failed, <code>status</code> will
	 * be an HTTP status code.
	 */
	public <T> T doPost(Type type, String url, Object object, String authorization) throws HttpClientException {
		return doRequest(type, url, object, authorization, "POST");
	}

	/**
	 * Performs a DELETE request.
	 *
	 * @param url The url to DELETE
	 * @throws HttpClientException
	 */
	public void doDelete(String url) throws HttpClientException {
		doRequest(Object.class, url, null, "", "DELETE");
	}

	/**
	 * Worker method
	 */
	private <T> T doRequest(Type type, String url, Object object, String authorization, String method)
	throws HttpClientException {
		HttpContent post = null;
		if (method.equals("POST"))
			post = new ByteArrayContent("application/json;charset=utf-8", gson.toJson(object).getBytes());

		HttpResponse response = null;
		try {
			HttpRequest request = requestFactory.buildRequest(method, new GenericUrl(url), post);
			if (authorization != null && authorization.length() > 0)
				request.getHeaders().setAuthorization(authorization);

			response = request.execute();
			return gson.fromJson(DescriptionStore.inputStreamToString(response.getContent()), type);
		} catch (HttpResponseException e) {
			throw new HttpClientException(e.getStatusCode(), e.getMessage());
		} catch (IOException|JsonParseException e) {
			throw new HttpClientException(e);
		} finally {
			try {
				if (response != null)
					response.disconnect();
			} catch (IOException e) { /* ignore */ }
		}
	}

	/**
	 * Asynchroniously performs a POST. See {@link #doPost(Type, String, Object)}.
	 */
	public <T> void post(final Type type, final String url, Object object, final HttpResultHandler<T> handler) {
		doAsyncRequest(type, url, object, "POST", handler);
	}

	/**
	 * Asynchroniously performs a POST, ignoring the response object from the server. See {@link #doPost(String, Object)}.
	 */
	public void post(final String url, Object object, final HttpResultHandler<Void> handler) {
		doAsyncRequest(Object.class, url, null, "POST", handler);
	}

	/**
	 * Asynchroniously performs a GET. See {@link #doGet(Type, String)}.
	 */
	public <T> void get(final Type type, final String url, final HttpResultHandler<T> handler) {
		doAsyncRequest(type, url, null, "GET", handler);
	}

	/**
	 * Worker method: wraps {@link #doRequest(Type, String, Object, String, String)} in an
	 * {@link AsyncTask}.
	 */
	private <T> void doAsyncRequest(final Type type, final String url, final Object object, final String method,
	                                final HttpResultHandler<T> handler) {
		new AsyncTask<Void, Void, HttpClientResult<T>>() {
			@Override
			protected HttpClientResult<T> doInBackground(Void... params) {
				try {
					T result = doRequest(type, url, object, "", method);
					return new HttpClientResult<>(result);
				} catch (HttpClientException e) {
					return new HttpClientResult<>(e);
				}
			}

			@Override
			protected void onPostExecute(HttpClientResult<T> result) {
				if (result.getObject() != null)
					handler.onSuccess(result.getObject());
				else
					handler.onError(result.getException());
			}
		}.execute();
	}
}
