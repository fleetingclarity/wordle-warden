WITH monthly_scores AS (
    SELECT user_id,
           user_name,
           DATE_TRUNC('month', puzzle_date) AS month,
           SUM(score) AS total_score,
           AVG(score) AS average_score,
           COUNT(*) AS submission_count,
           RANK() OVER (PARTITION BY DATE_TRUNC('month', puzzle_date) ORDER BY AVG(score) ASC) AS rank_by_total
    FROM wordle_scores
    GROUP BY user_id, user_name, DATE_TRUNC('month', puzzle_date)
),
filtered_scores AS (
    -- Exclude users who submitted less than 14 scores in a month
    SELECT *
    FROM monthly_scores
    WHERE submission_count >= 14
),
winners AS (
    -- Determine the winner for each month
    SELECT month,
           user_id,
           user_name,
           total_score,
           average_score,
           submission_count,
           RANK() OVER (PARTITION BY month ORDER BY average_score ASC) AS final_rank
    FROM filtered_scores
)
SELECT month,
       user_id,
       user_name,
       total_score,
       average_score,
       submission_count
FROM winners
WHERE final_rank = 1
ORDER BY month;
