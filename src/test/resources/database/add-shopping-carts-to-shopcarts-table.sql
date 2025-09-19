INSERT INTO shopping_carts (id, is_deleted)
SELECT u.id, 0
FROM users u
WHERE u.id = 3
  AND NOT EXISTS (SELECT 1 FROM shopping_carts sc WHERE sc.id = u.id);

INSERT INTO shopping_carts (id, is_deleted)
SELECT u.id, 0
FROM users u
WHERE u.id = 4
  AND NOT EXISTS (SELECT 1 FROM shopping_carts sc WHERE sc.id = u.id);
