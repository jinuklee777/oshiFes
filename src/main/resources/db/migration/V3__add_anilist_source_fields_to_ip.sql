ALTER TABLE character
    ADD COLUMN IF NOT EXISTS source_type varchar(255),
    ADD COLUMN IF NOT EXISTS external_id varchar(255),
    ADD COLUMN IF NOT EXISTS source_url varchar(255),
    ADD COLUMN IF NOT EXISTS is_auto_translated boolean NOT NULL DEFAULT false;

ALTER TABLE ip_title
    ADD COLUMN IF NOT EXISTS source_type varchar(255),
    ADD COLUMN IF NOT EXISTS external_id varchar(255),
    ADD COLUMN IF NOT EXISTS source_url varchar(255),
    ADD COLUMN IF NOT EXISTS is_auto_translated boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS extra jsonb;

CREATE UNIQUE INDEX IF NOT EXISTS ux_character_source_external_id
    ON character (source_type, external_id)
    WHERE source_type IS NOT NULL AND external_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_ip_title_source_external_id
    ON ip_title (source_type, external_id)
    WHERE source_type IS NOT NULL AND external_id IS NOT NULL;
