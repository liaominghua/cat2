package org.unidal.cat.spi.report.provider;

import java.io.IOException;
import java.util.List;

import org.unidal.cat.core.report.remote.RemoteReportContext;
import org.unidal.cat.spi.Report;
import org.unidal.cat.spi.report.ReportDelegate;
import org.unidal.cat.spi.report.ReportFilter;
import org.unidal.cat.spi.report.storage.ReportStorage;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

@Named(type = ReportProvider.class, value = HistoricalReportProvider.ID)
public class HistoricalReportProvider<T extends Report> implements ReportProvider<T> {
	public static final String ID = "historical";

	@Inject
	private ReportStorage<T> m_storage;

	@Override
	public boolean isEligible(RemoteReportContext ctx, ReportDelegate<T> delegate) {
		return ctx.getPeriod().isHistorical(ctx.getStartTime());
	}

	@Override
	public T getReport(RemoteReportContext ctx, ReportDelegate<T> delegate) throws IOException {
		Transaction t = Cat.getProducer().newTransaction("Service", "Historical");

		t.addData(ctx.toString());
		
		try {
			List<T> reports = m_storage.loadAll(delegate, ctx.getPeriod(), ctx.getStartTime(), ctx.getDomain());

			if (reports.isEmpty()) {
				t.setStatus(Message.SUCCESS);
				return null;
			} else {
				T aggregated = delegate.aggregate(ctx.getPeriod(), reports);
				ReportFilter<T> filter = ctx.getFilter();

				if (filter != null) {
					filter.tailor(ctx, aggregated);
				}

				t.setStatus(Message.SUCCESS);
				return aggregated;
			}
		} catch (IOException e) {
			Cat.logError(e);
			t.setStatus(e);
			throw e;
		} catch (RuntimeException e) {
			Cat.logError(e);
			t.setStatus(e);
			throw e;
		} catch (Error e) {
			Cat.logError(e);
			t.setStatus(e);
			throw e;
		} finally {
			t.complete();
		}
	}
}
