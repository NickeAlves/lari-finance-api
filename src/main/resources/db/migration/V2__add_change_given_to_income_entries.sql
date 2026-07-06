ALTER TABLE income_entries ADD COLUMN change_given BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE income_entries ADD COLUMN change_method VARCHAR(40);
