run:
	docker compose build postgres; docker compose build app;
	docker compose up -d postgres; docker compose up -d app;

tests:
	docker compose build postgres; docker compose build tests;
	docker compose up -d postgres; docker compose up tests;

stop:
	docker compose down;