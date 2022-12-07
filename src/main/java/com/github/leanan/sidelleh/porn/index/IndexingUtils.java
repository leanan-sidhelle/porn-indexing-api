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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * TODO:
 *
 */
public class IndexingUtils {
	
	/**
	 * TODO:
	 * @param doc
	 * @param searchPaths
	 * @return
	 * @throws MalformedURLException 
	 */
	public static URL getFirstHref(Document doc, String...searchPaths) throws MalformedURLException {
		for(String searchPath : searchPaths) {
			for(Element element : doc.selectXpath(searchPath)) {
				String href = element.attr("href");
				if(href!=null) {
					URL baseUrl = new URL(doc.baseUri());
					return new URL(baseUrl,href);
				}
			}
		}
		return null;
	}
	
	/**
	 * TODO:
	 * @param doc
	 * @param searchPaths
	 * @return
	 * @throws MalformedURLException 
	 */
	public static Element getFirstElement(Document doc, String...searchPaths) throws MalformedURLException {
		for(String searchPath : searchPaths) {
			for(Element element : doc.selectXpath(searchPath)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * TODO:
	 * @param doc
	 * @param rootSearchPaths
	 * @return
	 * @throws MalformedURLException 
	 */
	public static Collection<URL> getHrefs(Document doc,String...searchPaths) throws MalformedURLException {
		ArrayList<URL> results = new ArrayList<URL>();
		URL baseUrl = new URL(doc.baseUri());
		for(String searchPath : searchPaths) {
			for(Element element : doc.selectXpath(searchPath)) {
				String href = element.attr("href");
				if(href!=null) {
					results.add(new URL(baseUrl,href));
				}
			}
		}
		return results;
	}

	/**
	 * TODO:
	 * @param doc
	 * @param rootSearchPaths
	 * @return
	 */
	public static Collection<Element> getElements(Document doc,String...searchPaths) {
		ArrayList<Element> results = new ArrayList<Element>();
		for(String searchPath : searchPaths) {
			results.addAll(doc.selectXpath(searchPath));
		}
		return results;
	}
}
