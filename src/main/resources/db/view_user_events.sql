WITH users AS (
    SELECT DISTINCT user_id,
                    user_name
    FROM wordle_scores
)

SELECT user_name,
       timestamp,
       channel_id,
       team_id,
       event
FROM audit_trail aud
INNER JOIN users u
    ON u.user_id = aud.user_id;
