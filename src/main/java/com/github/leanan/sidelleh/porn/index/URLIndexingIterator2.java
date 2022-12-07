/**
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
 */
package com.github.leanan.sidelleh.porn.index;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leanan.sidelleh.util.LoggerUtils;

/**
 * TODO:
 *
 */
public abstract class URLIndexingIterator2 implements Iterator<URL> {
	
	/**
	 * TODO:
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(URLIndexingIterator2.class);

	/**
	 * TODO:
	 */
	protected final ObjectGraphIterator<Object> wrappedIterator;
	
	/**
	 * TODO:
	 * @param root
	 */
	public URLIndexingIterator2(URL root) {
		wrappedIterator = new ObjectGraphIterator<Object>(root,this::transform);
	}
	
	/**
	 * TODO:
	 * @param input
	 * @return
	 */
	protected Object transform(Object input) {
		URL url = (URL)input;
		LOGGER.debug("Transforming {}.",url);
		try {
			if(input==null) {
				return parseRootPages();
			} else if(isIndexPage(url)) {
				return parseIndexPage(url);
			} else if(isAlbumPage(url)) {
				return parseAlbumPage(url);
			} else if(isLeaf(url)) {
				return url;
			}
			throw new ClassCastException(); //TODO:better/more descriptive exception???
		} catch(Exception e) {
			LOGGER.error("Error transforming {}.",input);
			LoggerUtils.trace(LOGGER, e);
			return null;
		}
	}
	
	/**
	 * TODO:
	 * @return
	 * @throws IOException 
	 */
	protected URL parseRootPages() throws IOException {
		URL url = getBaseRootUrl();
		do {
			Document doc = Jsoup.parse(url,getTimeOut());
			//search for root href
			for(Element element : getRootAnchors(doc)) {
				if(isRootPage(element)) {
					String href = element.attr("href");
					return new URL(url,href);
				}
			}
			//get the next root page to check
			url = getNextRootPageLink(doc);
		} while(url!=null);
		return null;
	}

	/**
	 * TODO:
	 * @param doc
	 * @return
	 * @throws MalformedURLException 
	 */
	protected URL getNextRootPageLink(Document doc) throws MalformedURLException {
		return IndexingUtils.getFirstHref(doc,getRootPageNextSearchPaths());
	}

	/**
	 * TODO:
	 * @return
	 */
	protected abstract String[] getRootPageNextSearchPaths();

	/**
	 * TODO:
	 * @param element
	 * @return
	 */
	protected abstract boolean isRootPage(Element element);

	/**
	 * TODO:
	 * @param doc
	 * @return
	 */
	protected Collection<Element> getRootAnchors(Document doc) {
		return IndexingUtils.getElements(doc,getRootSearchPaths());
	}

	/**
	 * TODO:
	 * @return
	 */
	protected abstract String[] getRootSearchPaths();

	/**
	 * TODO:
	 * @return
	 */
	protected abstract URL getBaseRootUrl();

	/**
	 * TODO:
	 * @return
	 */
	protected int getTimeOut() {
		return 2; //default to 2s...
	}

	/**
	 * TODO:
	 * @param url
	 * @return
	 */
	protected abstract boolean isLeaf(URL url);

	/**
	 * TODO:
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	protected Iterator<URL> parseAlbumPage(URL url) throws IOException {
		Document doc = Jsoup.parse(url,getTimeOut());
		Collection<URL> results = IndexingUtils.getHrefs(doc, getAlbumSearchPaths());
		URL newUrl = getAlbumNextPageLink(doc);
		if(newUrl!=null) results.add(newUrl);
		return results.iterator();
	}

	/**
	 * TODO:
	 * @return
	 */
	protected abstract String[] getAlbumSearchPaths();

	/**
	 * TODO:
	 * @param doc
	 * @return
	 * @throws MalformedURLException 
	 */
	protected URL getAlbumNextPageLink(Document doc) throws MalformedURLException {
		return IndexingUtils.getFirstHref(doc,getAlbumPageNextSearchPaths());
	}

	/**
	 * TODO:
	 * @return
	 */
	protected abstract String[] getAlbumPageNextSearchPaths();

	/**
	 * TODO:
	 * @param url
	 * @return
	 */
	protected abstract boolean isAlbumPage(URL url);

	/**
	 * TODO:
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	protected Iterator<URL> parseIndexPage(URL url) throws IOException {
		Document doc = Jsoup.parse(url,getTimeOut());
		Collection<URL> results = IndexingUtils.getHrefs(doc, getIndexSearchPaths());
		URL newUrl = getIndexNextPageLink(doc);
		if(newUrl!=null) results.add(newUrl);
		return results.iterator();
	}

	/**
	 * TODO:
	 * @param doc
	 * @return
	 * @throws MalformedURLException 
	 */
	protected URL getIndexNextPageLink(Document doc) throws MalformedURLException {
		return IndexingUtils.getFirstHref(doc,getIndexPageNextSearchPaths());
	}

	protected abstract String[] getIndexPageNextSearchPaths();

	/**
	 * TODO:
	 * @return
	 */
	protected abstract String[] getIndexSearchPaths();

	/**
	 * TODO:
	 * @param url
	 * @return
	 */
	protected abstract boolean isIndexPage(URL url);

	@Override
	public boolean hasNext() {
		return wrappedIterator.hasNext();
	}

	@Override
	public URL next() {
		return (URL)wrappedIterator.next();
	}

}
