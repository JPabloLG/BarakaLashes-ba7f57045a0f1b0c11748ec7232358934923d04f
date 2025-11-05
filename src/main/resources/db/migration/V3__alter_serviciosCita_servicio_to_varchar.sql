-- Ensure the serviciosCita.servicio column stores enum names as text
-- This fixes "Data truncated for column 'servicio'" when persisting enum values
ALTER TABLE serviciosCita MODIFY COLUMN servicio VARCHAR(50);
