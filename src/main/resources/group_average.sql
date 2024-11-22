WITH user_averages AS (
    SELECT user_name,
           AVG(score) AS average_score_per_person
    FROM wordle_scores
    GROUP BY user_name
),
group_average AS (
    SELECT AVG(average_score_per_person) AS group_average_score
    FROM user_averages
)
SELECT ua.user_name,
       ua.average_score_per_person,
       ga.group_average_score,
       ua.average_score_per_person - ga.group_average_score AS difference_from_group
FROM user_averages ua
CROSS JOIN group_average ga
ORDER BY ua.user_name;
