package idevgame.meteor.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;


public class RedisRank extends RedisClient {

	private static final float MAX_LIMIT = 1.5f;
	private static final float MIN_LIMIT = 1.2f;


	private Map<String, Rank> ranks = new HashMap<>();


	public RedisRank(String ip, int port,String password) {
		super(ip, port, 0,password);
	}


	public void initRank(String rankName, int maxSize) throws Exception {
		Jedis jedis = null;

		try {
			jedis = getConnect();
			
			long size = jedis.zcard(rankName);
			Set<Tuple> set = jedis.zrangeWithScores(rankName, 0, 0);
			double min = 0;
			if (!set.isEmpty() && size >= maxSize) {
				min = set.iterator().next().getScore();
			}
			ranks.put(rankName, new Rank(maxSize, size, min));

		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}


	private void resize(Jedis jedis, Rank rank, String rankName) {
		synchronized (rank) {
			long limit = (long) (rank.maxSize * MAX_LIMIT);
			long minLinit  = (long) (rank.maxSize * MIN_LIMIT);
			if (rank.size.get() > limit) {
				int point = (int) (limit - minLinit);
				Set<Tuple> set = jedis.zrangeWithScores(rankName, point, point);
				if(set != null) {
					Tuple min = set.iterator().next();
					rank.min = min.getScore();
					jedis.zremrangeByScore(rankName, 0, rank.min);
					Long size = jedis.zcard(rankName);
					rank.size.set(size);
				}
			} else if (rank.size.get() < minLinit) { //閹烘帟顢戝婊勭梾濠娾槄绱濋崣顖欎簰缂佈呯敾濞ｈ濮為弫鐗堝祦
				rank.min = 0;
			}
		}
	}


	public void add(String rankName, String key, double score , boolean isForceFlush) {
		Jedis jedis = null;
		Rank rank = ranks.get(rankName);
		try {
			jedis = getConnect();
			if(isForceFlush && jedis.zscore(rankName, key) != null) {
				jedis.zrem(rankName, key);
				rank.size.decrementAndGet();//鐠佲剝鏆� - 1
			}
			if (score > rank.min) {
				if(jedis.zscore(rankName, key) == null) {//濞屸剝婀侀弫鐗堝祦閿涘ize + 1
					rank.size.incrementAndGet();
				}
				jedis.zadd(rankName, score, key);
				resize(jedis, rank, rankName);
			} 
		}catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}	
	}
	

	public long getRankScore(String rankName, String key){
		Jedis jedis = null;
		
		try {
			jedis = getConnect();
			if(jedis == null){
				return -1;
			}
			Double score = jedis.zscore(rankName, key);
			if(score == null){
				return -1;
			}
			return score.longValue();
		}catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}	
	}
	

	public Long getRankRanking(String rankName, String key){
		Jedis jedis = null;
		
		try {
			jedis = getConnect();
			return jedis.zrevrank(rankName, key);
		}catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}	
	}


	public void addAll(String rankName, Map<String, Double> map) {
		Jedis jedis = null;
		Rank rank = ranks.get(rankName);
		Map<String, Double> map1 = new HashMap<>();
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			if (entry.getValue() > rank.min) {
				map1.put(entry.getKey(), entry.getValue());
			}
		}

		try {
			if(map1.size() > 0) //缁岀儤鏆熼幑顔荤瑝閸旂姴鍙�
			{
				jedis = getConnect();
				jedis.zadd(rankName, map1);
				rank.size.addAndGet(map1.size());
				resize(jedis, rank, rankName);
			}
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}

	public List<RankScoreObject> range(String rankName, int from, int to) {
		Jedis jedis = null;
		try {
			jedis = getConnect();
			Set<Tuple> set = jedis.zrevrangeWithScores(rankName, from, to);
			List<RankScoreObject> res = new ArrayList<>();
			int pos = from;

			for (Tuple tuple : set) {
				res.add(new RankScoreObject(tuple.getElement(), tuple.getScore(), pos++));
			}

			return res;
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}
	
	public Long getRankNum(String rankName)
	{
		Jedis jedis = null;
		try {
			jedis = getConnect();
			if(jedis == null){
				return 0l;
			}
			Long rankNum = jedis.zcard(rankName);
			if(rankNum == null){
				return 0l;
			}
			return jedis.zcard(rankName);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
		
	}
	
	public void del(String key) {
		Jedis jedis = null;
		try {
			jedis = getConnect();
			ranks.remove(key);
			jedis.del(key);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}
	
	public void deleteByTypeAndPlayer(String type, String playerId) {
		Jedis jedis = null;
		try {
			jedis = getConnect();
			jedis.zrem(type, playerId);
		} catch (RuntimeException e) {
			returnExceptionConnection(jedis);
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis);
		}
	}
	
	private static class Rank {
		private int maxSize;
		private AtomicLong size;
		private double min;

		private Rank(int maxSize, long size, double min) {
			this.maxSize = maxSize;
			this.size = new AtomicLong(size);
			this.min = min * 0.9;
		}

	}

	public static class RankScoreObject {
		private String key;
		private Double score;
		private int rank;

		public RankScoreObject(String key, Double score, int rank) {
			this.key = key;
			this.score = score;
			this.rank = rank;
		}

		public String getKey() {
			return key;
		}

		public Double getScore() {
			return score;
		}

		public int getRank() {
			return rank;
		}

	}
	
}
