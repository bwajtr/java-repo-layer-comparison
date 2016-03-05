/**
 * This class is generated by jOOQ
 */
package com.clevergang.dbtests.service.repository.impl.jooq.generated.tables;


import com.clevergang.dbtests.service.repository.impl.jooq.generated.Public;
import com.clevergang.dbtests.service.repository.impl.jooq.generated.tables.records.TimeTestRecord;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TimeTest extends TableImpl<TimeTestRecord> {

	private static final long serialVersionUID = 631031202;

	/**
	 * The reference instance of <code>public.time_test</code>
	 */
	public static final TimeTest TIME_TEST = new TimeTest();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TimeTestRecord> getRecordType() {
		return TimeTestRecord.class;
	}

	/**
	 * The column <code>public.time_test.ts</code>.
	 */
	public final TableField<TimeTestRecord, Timestamp> TS = createField("ts", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>public.time_test.ts_default</code>.
	 */
	public final TableField<TimeTestRecord, Timestamp> TS_DEFAULT = createField("ts_default", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * Create a <code>public.time_test</code> table reference
	 */
	public TimeTest() {
		this("time_test", null);
	}

	/**
	 * Create an aliased <code>public.time_test</code> table reference
	 */
	public TimeTest(String alias) {
		this(alias, TIME_TEST);
	}

	private TimeTest(String alias, Table<TimeTestRecord> aliased) {
		this(alias, aliased, null);
	}

	private TimeTest(String alias, Table<TimeTestRecord> aliased, Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimeTest as(String alias) {
		return new TimeTest(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TimeTest rename(String name) {
		return new TimeTest(name, null);
	}
}
