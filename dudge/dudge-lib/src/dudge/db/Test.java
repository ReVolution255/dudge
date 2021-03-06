/*
 * Tests.java
 *
 * Created on 12 Апрель 2007 г., 19:36
 *
 */
package dudge.db;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 * Entity class Tests
 *
 * @author Michael Antonov
 */
@Entity(name = "Test")
@Table(
		name = "tests",
		uniqueConstraints =
		@UniqueConstraint(columnNames = {"problem_id", "test_number"}))
@NamedQueries({
	@NamedQuery(name = "Test.findByTestId", query = "SELECT t FROM Test t WHERE t.testId = :testId"),
	@NamedQuery(name = "Test.findByProblemId", query = "SELECT t FROM Test t WHERE t.problem.problemId = :problemId"),
	@NamedQuery(name = "Test.findByNumber", query = "SELECT t FROM Test t WHERE t.testNumber = :testNumber"),
	@NamedQuery(name = "Test.findByNumberAndProblemId", query = "SELECT t FROM Test t WHERE t.testNumber = :testNumber AND t.problem.problemId = :problemId")
})
public class Test implements Serializable, Comparable<Test> {

	public static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Test.class.toString());
	@SequenceGenerator(name = "TestIdGen", sequenceName = "tests_test_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TestIdGen")
	@Id
	@Column(name = "test_id", nullable = false)
	private int testId;
	@Column(name = "test_number", nullable = false)
	private int testNumber;
	@Lob
	@Column(name = "input_data", nullable = false)
	private String inputData;
	@Lob
	@Column(name = "output_data", nullable = false)
	private String outputData;
	@JoinColumn(name = "problem_id", referencedColumnName = "problem_id", updatable = false)
	@ManyToOne
	private Problem problem;

	/**
	 * Creates a new instance of Tests
	 */
	public Test() {
	}

	/**
	 * Creates a new instance of Tests with the specified values.
	 *
	 * @param inputData the inputData of the Tests
	 * @param outputData the outputData of the Tests
	 */
	public Test(String inputData, String outputData) {
		this.inputData = inputData;
		this.outputData = outputData;
	}

	/**
	 * Gets the inputData of this Tests.
	 *
	 * @return the inputData
	 */
	public String getInputData() {
		return this.inputData;
	}

	/**
	 * Sets the inputData of this Tests to the specified value.
	 *
	 * @param inputData the new inputData
	 */
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	/**
	 * Gets the outputData of this Tests.
	 *
	 * @return the outputData
	 */
	public String getOutputData() {
		return this.outputData;
	}

	/**
	 * Sets the outputData of this Test to the specified value.
	 *
	 * @param outputData the new outputData
	 */
	public void setOutputData(String outputData) {
		this.outputData = outputData;
	}

	/**
	 * Gets the problem of this Test.
	 *
	 * @return the problem
	 */
	public Problem getProblem() {
		return this.problem;
	}

	/**
	 * Sets the problem of this Test to the specified value.
	 *
	 * @param problem the new problem
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	/**
	 * Determines whether another object is equal to this Tests. The result is
	 * <code>true</code> if and only if the argument is not null and is a Tests object that has the same id field values as this object.
	 *
	 * @param object the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Test)) {
			return false;
		}
		Test other = (Test) object;
		if (this.testId != other.testId) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += this.testId;
		return hash;
	}
	

	/**
	 * Returns a string representation of the object. This implementation constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "dudgedb.Test[testsPK=" + testId + "]";
	}

	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public int getTestNumber() {
		return testNumber;
	}

	public void setTestNumber(Integer testNumber) {
		this.testNumber = testNumber;
	}

	@Override
	public int compareTo(Test other) {
		if (this.getTestId() != other.getTestId()) {
			return Integer.valueOf(this.getTestId()).compareTo(Integer.valueOf(other.getTestId()));
		}

		return Integer.valueOf(this.getTestNumber()).compareTo(Integer.valueOf(other.getTestNumber()));
	}
}
