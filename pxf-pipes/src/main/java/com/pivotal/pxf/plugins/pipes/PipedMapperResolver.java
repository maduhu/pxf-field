package com.pivotal.pxf.plugins.pipes;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configuration.IntegerRanges;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.security.Credentials;
import org.apache.log4j.Logger;

import com.pivotal.pxf.api.OneField;
import com.pivotal.pxf.api.OneRow;
import com.pivotal.pxf.api.ReadResolver;
import com.pivotal.pxf.api.io.DataType;
import com.pivotal.pxf.api.utilities.InputData;
import com.pivotal.pxf.api.utilities.Plugin;
import com.pivotal.pxf.plugins.pipes.PipedMapperResolver.PipedMapper.PipedMapContextImpl;

public class PipedMapperResolver extends Plugin implements ReadResolver {

	private Mapper<Object, Object, Object, Text> mapper = null;

	private List<OneField> record = new ArrayList<>();
	private PipedMapContextImpl context = null;

	private static final Logger LOG = Logger
			.getLogger(PipedMapperResolver.class);

	public PipedMapperResolver(InputData data) throws Exception {
		super(data);

		mapper = PxfPipesUtil.getMapperClass(data);

		if (mapper == null) {
			throw new InvalidParameterException(
					"Must set MAPPER to a Java class that is on the CLASSPATH");
		} else {
			LOG.debug("Mapper is " + mapper);
		}

		PipedMapper mapper = new PipedMapper();
		context = mapper.getContext();
	}

	@Override
	public List<OneField> getFields(OneRow row) throws Exception {
		record.clear();

		context.reset();
		context.setCurrentKey(row.getKey());
		context.setCurrentValue(row.getData());

		mapper.run(context);

		record.add(new OneField(DataType.VARCHAR.getOID(), context.getRow().trim()));
		return record;
	}

	public class PipedMapper extends Mapper<Object, Object, Object, Text> {

		public PipedMapContextImpl getContext() {
			return new PipedMapContextImpl();
		}

		public class PipedMapContextImpl extends
				Mapper<Object, Object, Object, Text>.Context {

			private String row = null;
			private boolean firstCall = true;
			private Object key = null;
			private Object value = null;

			@Override
			public void write(Object key, Text value) throws IOException,
					InterruptedException {
				row = value.toString();
			}

			public String getRow() {
				return row;
			}

			@Override
			public boolean nextKeyValue() throws IOException,
					InterruptedException {
				if (firstCall) {
					firstCall = false;
					return true;
				} else {
					return false;
				}
			}

			public void setCurrentKey(Object key) {
				this.key = key;
			}
			
			@Override
			public Object getCurrentKey() throws IOException,
					InterruptedException {
				return key;
			}

			public void setCurrentValue(Object value) {
				this.value = value;
			}

			@Override
			public Object getCurrentValue() throws IOException,
					InterruptedException {
				return value;
			}

			public void reset() {
				firstCall = true;
			}

			@Override
			public InputSplit getInputSplit() {
				return null;
			}

			@Override
			public OutputCommitter getOutputCommitter() {
				return null;
			}

			@Override
			public TaskAttemptID getTaskAttemptID() {
				return null;
			}

			@Override
			public void setStatus(String paramString) {

			}

			@Override
			public String getStatus() {
				return null;
			}

			@Override
			public float getProgress() {
				return 0;
			}

			@Override
			public Counter getCounter(Enum<?> paramEnum) {
				return null;
			}

			@Override
			public Counter getCounter(String paramString1, String paramString2) {
				return null;
			}

			@Override
			public Configuration getConfiguration() {
				return null;
			}

			@Override
			public Credentials getCredentials() {
				return null;
			}

			@Override
			public JobID getJobID() {
				return null;
			}

			@Override
			public int getNumReduceTasks() {
				return 0;
			}

			@Override
			public Path getWorkingDirectory() throws IOException {
				return null;
			}

			@Override
			public Class<?> getOutputKeyClass() {
				return null;
			}

			@Override
			public Class<?> getOutputValueClass() {
				return null;
			}

			@Override
			public Class<?> getMapOutputKeyClass() {
				return null;
			}

			@Override
			public Class<?> getMapOutputValueClass() {
				return null;
			}

			@Override
			public String getJobName() {
				return null;
			}

			@Override
			public Class<? extends InputFormat<?, ?>> getInputFormatClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public Class<? extends Mapper<?, ?, ?, ?>> getMapperClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public Class<? extends Reducer<?, ?, ?, ?>> getCombinerClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public Class<? extends Reducer<?, ?, ?, ?>> getReducerClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public Class<? extends OutputFormat<?, ?>> getOutputFormatClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public Class<? extends Partitioner<?, ?>> getPartitionerClass()
					throws ClassNotFoundException {
				return null;
			}

			@Override
			public RawComparator<?> getSortComparator() {
				return null;
			}

			@Override
			public String getJar() {
				return null;
			}

			@Override
			public RawComparator<?> getGroupingComparator() {
				return null;
			}

			@Override
			public boolean getJobSetupCleanupNeeded() {
				return false;
			}

			@Override
			public boolean getTaskCleanupNeeded() {
				return false;
			}

			@Override
			public boolean getProfileEnabled() {
				return false;
			}

			@Override
			public String getProfileParams() {
				return null;
			}

			@Override
			public IntegerRanges getProfileTaskRange(boolean paramBoolean) {
				return null;
			}

			@Override
			public String getUser() {
				return null;
			}

			@Override
			public boolean getSymlink() {
				return false;
			}

			@Override
			public Path[] getArchiveClassPaths() {
				return null;
			}

			@Override
			public URI[] getCacheArchives() throws IOException {
				return null;
			}

			@Override
			public URI[] getCacheFiles() throws IOException {
				return null;
			}

			@Override
			public Path[] getLocalCacheArchives() throws IOException {
				return null;
			}

			@Override
			public Path[] getLocalCacheFiles() throws IOException {
				return null;
			}

			@Override
			public Path[] getFileClassPaths() {
				return null;
			}

			@Override
			public String[] getArchiveTimestamps() {
				return null;
			}

			@Override
			public String[] getFileTimestamps() {
				return null;
			}

			@Override
			public int getMaxMapAttempts() {
				return 0;
			}

			@Override
			public int getMaxReduceAttempts() {
				return 0;
			}

			@Override
			public void progress() {

			}
		}
	}
}