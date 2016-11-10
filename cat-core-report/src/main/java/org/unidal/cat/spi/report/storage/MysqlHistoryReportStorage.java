package org.unidal.cat.spi.report.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.unidal.cat.core.report.dal.HistoryReportContentDao;
import org.unidal.cat.core.report.dal.HistoryReportContentDo;
import org.unidal.cat.core.report.dal.HistoryReportContentEntity;
import org.unidal.cat.core.report.dal.HistoryReportDao;
import org.unidal.cat.core.report.dal.HistoryReportDo;
import org.unidal.cat.core.report.dal.HistoryReportEntity;
import org.unidal.cat.service.CompressionService;
import org.unidal.cat.spi.Report;
import org.unidal.cat.spi.ReportPeriod;
import org.unidal.cat.spi.report.ReportDelegate;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.helper.Inets;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;

@Named(type = ReportStorage.class, value = MysqlHistoryReportStorage.ID)
public class MysqlHistoryReportStorage<T extends Report> implements ReportStorage<T> {
	public static final String ID = "mysql-history";

	@Inject
	private CompressionService m_compression;

	@Inject
	private HistoryReportDao m_dao;

	@Inject
	private HistoryReportContentDao m_contentDao;

	@Override
	public List<T> loadAll(ReportDelegate<T> delegate, ReportPeriod period, Date startTime, String domain)
	      throws IOException {
		try {
			List<T> reports = new ArrayList<T>(1);
			HistoryReportDo dr = m_dao.findByDomainAndTypeAndNameAndStartTime(domain, period.getId(), delegate.getName(),
			      startTime, HistoryReportEntity.READSET_FULL);
			HistoryReportContentDo content = m_contentDao.findByPK(dr.getId(), HistoryReportContentEntity.READSET_FULL);
			InputStream in = new ByteArrayInputStream(content.getContent());
			InputStream cin = m_compression.decompress(in);
			T report = delegate.readStream(cin);

			reports.add(report);
			return reports;
		} catch (DalNotFoundException e) {
			return Collections.emptyList();
		} catch (DalException e) {
			throw new IOException(String.format("Unable to load %s reports(%s) from MySQL!", period.getName(),
			      delegate.getName()), e);
		}
	}

	@Override
	public List<T> loadAllByDateRange(ReportDelegate<T> delegate, ReportPeriod period, Date startTime, Date endTime,
	      String domain) throws IOException {
		try {
			List<HistoryReportDo> hrs = m_dao.findAllByDomainAndTypeAndNameAndDateRange(domain, period.getId(),
			      delegate.getName(), startTime, endTime, HistoryReportEntity.READSET_FULL);
			List<T> reports = new ArrayList<T>(hrs.size());

			for (HistoryReportDo hr : hrs) {
				try {
					HistoryReportContentDo content = m_contentDao.findByPK(hr.getId(),
					      HistoryReportContentEntity.READSET_FULL);
					InputStream in = new ByteArrayInputStream(content.getContent());
					InputStream cin = m_compression.decompress(in);
					T report = delegate.readStream(cin);

					reports.add(report);
				} catch (Exception e) {
					Cat.logError(e);
				}
			}

			return reports;
		} catch (DalException e) {
			throw new IOException(String.format("Unable to load hourly reports(%s) from MySQL! " + e, delegate.getName()),
			      e);
		}
	}

	@Override
	public void store(ReportDelegate<T> delegate, ReportPeriod period, T report, int index)
	      throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(2 * 1024 * 1024);
		OutputStream cout = m_compression.compress(out);

		delegate.writeStream(cout, report);
		cout.close();

		try {
			HistoryReportDo r = m_dao.createLocal();
			String ip = Inets.IP4.getLocalHostAddress();

			r.setType(period.getId());
			r.setName(delegate.getName());
			r.setDomain(report.getDomain());
			r.setStartTime(report.getStartTime());
			r.setIp(ip);

			m_dao.insert(r);

			HistoryReportContentDo rc = m_contentDao.createLocal();

			rc.setReportId(r.getId());
			rc.setFormat(11);
			rc.setContent(out.toByteArray());

			m_contentDao.insert(rc);
		} catch (DalException e) {
			throw new IOException(String.format("Unable to store %s report(%s) to MySQL!", period.getName(),
			      delegate.getName()), e);
		}
	}
}
