WITH daily_scores AS (
    -- Ensure only one submission per user per day is counted. Ensure only the worst score is considered
    SELECT user_id,
           user_name,
           DATE_TRUNC('day', puzzle_date) AS day,
           MAX(score) AS daily_score
    FROM wordle_scores
    GROUP BY user_id, user_name, DATE_TRUNC('day', puzzle_date)
),
weekly_scores AS (
    -- Aggregate scores by week
    SELECT user_id,
           user_name,
           DATE_TRUNC('week', day) AS week_start,
           SUM(daily_score) AS total_score,
           AVG(daily_score) AS average_score,
           COUNT(*) AS submission_count,
           RANK() OVER (PARTITION BY DATE_TRUNC('week', day) ORDER BY AVG(daily_score) ASC) AS rank_by_total
    FROM daily_scores
    GROUP BY user_id, user_name, DATE_TRUNC('week', day)
),
filtered_scores AS (
    -- Exclude users who submitted on fewer than 3 distinct days in a week
    SELECT *
    FROM weekly_scores
    WHERE submission_count >= 4
),
winners AS (
    -- Determine the winner for each week
    SELECT TO_CHAR(week_start, 'YYYY-MM-DD') AS week,
           user_id,
           user_name,
           total_score,
           average_score,
           submission_count,
           RANK() OVER (PARTITION BY week_start ORDER BY average_score ASC) AS final_rank
    FROM filtered_scores
)
SELECT week,
       user_id,
       user_name,
       total_score,
       average_score,
       submission_count
FROM winners
WHERE final_rank = 1
ORDER BY week;
