/**
 * This class is generated by jOOQ
 */
package com.clevergang.dbtests.service.repository.impl.jooq.generated;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in public
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

	/**
	 * The sequence <code>public.company_pid_seq</code>
	 */
	public static final Sequence<Long> COMPANY_PID_SEQ = new SequenceImpl<Long>("company_pid_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

	/**
	 * The sequence <code>public.department_pid_seq</code>
	 */
	public static final Sequence<Long> DEPARTMENT_PID_SEQ = new SequenceImpl<Long>("department_pid_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

	/**
	 * The sequence <code>public.employee_pid_seq</code>
	 */
	public static final Sequence<Long> EMPLOYEE_PID_SEQ = new SequenceImpl<Long>("employee_pid_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

	/**
	 * The sequence <code>public.product_category_pid_seq</code>
	 */
	public static final Sequence<Long> PRODUCT_CATEGORY_PID_SEQ = new SequenceImpl<Long>("product_category_pid_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

	/**
	 * The sequence <code>public.product_pid_seq</code>
	 */
	public static final Sequence<Long> PRODUCT_PID_SEQ = new SequenceImpl<Long>("product_pid_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
