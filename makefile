.PHONY: up stop down up-dev stop-dev down-dev

up:
	docker compose -p insurance-claims-event-driven up -d --build

stop:
	docker compose -p insurance-claims-event-driven stop

down:
	docker compose -p insurance-claims-event-driven down

# if you need debugging tools
up-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml -p insurance-claims-event-driven up -d --build

stop-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml -p insurance-claims-event-driven stop

down-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml -p insurance-claims-event-driven down
