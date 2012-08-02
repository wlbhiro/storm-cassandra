package backtype.storm.contrib.cassandra.bolt;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.contrib.cassandra.bolt.determinable.ColumnFamilyDeterminable;
import backtype.storm.contrib.cassandra.bolt.determinable.ColumnsDeterminable;
import backtype.storm.contrib.cassandra.bolt.determinable.DefaultColumnFamilyDeterminable;
import backtype.storm.contrib.cassandra.bolt.determinable.DefaultColumnsDeterminable;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;

@SuppressWarnings("serial")
public abstract class BatchingCassandraBolt extends AbstractBatchingBolt implements CassandraConstants {
    private static final Logger LOG = LoggerFactory.getLogger(BatchingCassandraBolt.class);

    public static enum AckStrategy {
        ACK_IGNORE, ACK_ON_RECEIVE, ACK_ON_WRITE;
    }

    protected AckStrategy ackStrategy = AckStrategy.ACK_IGNORE;

    protected OutputCollector collector;

    private Fields declaredFields;

    protected ColumnFamilyDeterminable cfDeterminable;
    protected ColumnsDeterminable colsDeterminable;

    // protected RowKeyDeterminable rkDeterminable;

    public BatchingCassandraBolt(String columnFamily) {
        this(new DefaultColumnFamilyDeterminable(columnFamily), new DefaultColumnsDeterminable());
    }

    public BatchingCassandraBolt(ColumnFamilyDeterminable cfDeterminable, ColumnsDeterminable colsDeterminable) {
        this.cfDeterminable = cfDeterminable;
        // this.rkDeterminable = rkDeterminable;
    }

    public void setAckStrategy(AckStrategy strategy) {
        this.ackStrategy = strategy;
    }

    /*
     * IRichBolt Implementation
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        LOG.debug("Preparing...");
        this.collector = collector;
        if (this.ackStrategy == AckStrategy.ACK_ON_RECEIVE) {
            super.setAckOnReceive(true);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        if (this.declaredFields != null) {
            declarer.declare(this.declaredFields);
        }

    }

}
