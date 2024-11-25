WITH monthly_submissions AS (
    SELECT user_id,
           user_name,
           DATE_TRUNC('month', puzzle_date) AS month,
           COUNT(*) AS total_submissions
    FROM wordle_scores
    GROUP BY user_id, user_name, DATE_TRUNC('month', puzzle_date)
),
submissions_with_difference AS (
    SELECT user_id,
           user_name,
           TO_CHAR(month, 'Month') as month_name,
           total_submissions,
           total_submissions - LAG(total_submissions) OVER (PARTITION BY user_id ORDER BY month) AS difference_from_previous_month
    FROM monthly_submissions
)
SELECT user_name,
       month_name,
       total_submissions,
       COALESCE(difference_from_previous_month, 0) AS difference_from_previous_month
FROM submissions_with_difference
ORDER BY user_id, month_name;
