package com.luke.volley.extra;

import org.pmw.tinylog.Logger;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by cplu on 2016/3/8.
 * Manager the test of network speed, bandwidth, and so on
 */
public class NetworkTester {

	private static final int MAX_CASE_COUNT = 50;

	private NetworkTester() {

	}

	private static NetworkTester s_instance;

	public static synchronized NetworkTester instance() {
		if (s_instance == null) {
			synchronized (NetworkTester.class) {
				// Double check
				if (s_instance == null) {
					s_instance = new NetworkTester();
				}
			}
		}
		return s_instance;
	}

	private LinkedList<Integer> m_bandwidthList = new LinkedList<>();

	public void addTestCase(int length, long networkTimeMs) {
		if ((length >> 10) > 100) {  /// greater than 100KB
			if (networkTimeMs > 100) {   /// greater than 100ms
				Logger.debug("Test case with length {} and time {} added", length, networkTimeMs);
				m_bandwidthList.addLast((int) (length / networkTimeMs));
				if(m_bandwidthList.size() > MAX_CASE_COUNT) {
					m_bandwidthList.removeFirst();
				}
				return;
			}
		}
		Logger.debug("Test case is not favourable with length {} and time {}", length, networkTimeMs);
	}

	public void resetTester() {
		m_bandwidthList.clear();
	}

	public int getBandwidth() {
		Iterator<Integer> it = m_bandwidthList.iterator();
		int result = 0;
		int count = 0;
		while (it.hasNext()) {
			result += it.next();
			count++;
		}
		if (count > 0) {
			return result / count * 1000;   /// in KB/s
		}
		return 0;
	}
}
