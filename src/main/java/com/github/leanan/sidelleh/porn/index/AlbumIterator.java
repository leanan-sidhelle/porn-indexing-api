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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO:import com.github.leanan.sidelleh.util.LoggerUtils;

/**
 * TODO:
 *
 */
public abstract class AlbumIterator extends URLIndexingIterator {
	
	/**
	 * TODO:
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AlbumIterator.class);

	/**
	 * TODO:
	 */
	protected final URL indexUrl;

	/**
	 * TODO:
	 * @param root
	 */
	public AlbumIterator(URL root) {
		super(root);
		this.indexUrl = root; //TODO:can this be replaced by getRoot() ???
	}
	
	/**
	 * TODO:
	 * @return
	 */
	protected int getTimeOut() {
		return 10000; //default to 10s...
	}

	@Override
	protected Iterator<URL> parseAlbumPage(URL url) {
		try {
			Document doc = Jsoup.parse(url,getTimeOut());
			ArrayList<URL> results = new ArrayList<URL>();
			for(String searchPath : getLeafSearchPaths()) {
				for(Element elem : doc.selectXpath(searchPath)) {
					String href = elem.attr("href");
					if(href!=null && !href.isBlank()) {
						URL newUrl = new URL(url,href);
						results.add(newUrl);
					}
				}
			}
			return results.iterator();
		} catch(IOException e) {
			LOGGER.warn("Error parsing {}.",url);
			//TODO:LoggerUtils.trace(LOGGER,e);
			return null;
		}
	}
	
	/**
	 * TODO:
	 * @return
	 */
	protected abstract List<String> getLeafSearchPaths();

	@Override
	protected Iterator<URL> parseIndexPage(URL url) {
		try {
			Document doc = Jsoup.parse(url,getTimeOut());
			ArrayList<URL> results = new ArrayList<URL>();
			for(String searchPath : getBranchSearchPaths()) {
				for(Element elem : doc.selectXpath(searchPath)) {
					String href = elem.attr("href");
					if(href!=null && !href.isBlank()) {
						URL newUrl = new URL(url,href);
						results.add(newUrl);
					}
				}
			}
			return results.iterator();
		} catch(IOException e) {
			LOGGER.warn("Error parsing {}.",url);
			//TODO:LoggerUtils.trace(LOGGER,e);
			return null;
		}
	}

	/**
	 * TODO:
	 * @return
	 */
	protected abstract List<String> getBranchSearchPaths();
	
}
