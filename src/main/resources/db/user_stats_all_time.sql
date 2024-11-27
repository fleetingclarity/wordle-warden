WITH user_score_counts AS (
    -- Count each score for each user
    SELECT user_id,
           user_name,
           score,
           COUNT(*) AS score_count
    FROM wordle_scores
    GROUP BY user_id, user_name, score
),
     user_total_counts AS (
         -- Calculate the total number of scores per user
         SELECT user_id,
                user_name,
                SUM(score_count) AS total_count
         FROM user_score_counts
         GROUP BY user_id, user_name
     ),
     user_score_percentages AS (
         -- Calculate the percentage of each score
         SELECT usc.user_id,
                usc.user_name,
                usc.score,
                usc.score_count,
                utc.total_count,
                (usc.score_count::DECIMAL / utc.total_count * 100) AS percentage
         FROM user_score_counts usc
         JOIN user_total_counts utc
           ON usc.user_id = utc.user_id
     )
-- Output the report
SELECT user_name,
       score,
       score_count,
       total_count,
       ROUND(percentage, 4) AS percentage
FROM user_score_percentages
ORDER BY user_name, score;
