# Gathering API (MVP)

Spring Boot REST API to organize recurring activities and collect yes/no RSVPs per sub-activity.

## MVP decisions implemented

- No auth (public MVP)
- Join by occurrence code
- Yes/No votes only
- Duplicated votes allowed
- Single timezone default (`America/Argentina/Buenos_Aires`)
- Recurrence supported: `ONCE`, `DAILY`, `WEEKLY`, `EVERY_X_DAYS`
- Email opt-in reminders: voters can choose to receive email notifications 24h before events

## Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway migrations
- Spring Data JPA
- Spring Mail (SMTP reminders)
- OpenAPI UI: `/swagger-ui/index.html`

## Run locally

1. Start PostgreSQL:
   ```bash
  docker compose up -d
   ```
2. (Optional) Configure SMTP for email reminders:
   ```bash
   export MAIL_ENABLED=true
   export SMTP_USERNAME=your-email@gmail.com
   export SMTP_PASSWORD=your-app-password
   export MAIL_FROM=noreply@gathering.local
   ```
3. Run API:
   ```bash
  ./mvnw spring-boot:run
   ```
   or
   ```bash
  mvn spring-boot:run
   ```

## Main endpoints

- `POST /api/templates`
- `GET /api/templates`
- `POST /api/templates/{templateId}/occurrences/generate`
- `GET /api/occurrences?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/public/join/{joinCode}`
- `POST /api/public/occurrences/{occurrenceId}/votes`
- `DELETE /api/public/votes/{voteId}`
- `POST /api/admin/reminders/send?date=YYYY-MM-DD`

## Example create template

`POST /api/templates`

```json
{
  "name": "Soccer Tuesday",
  "description": "Weekly soccer with optional dinner",
  "timezone": "America/Argentina/Buenos_Aires",
  "recurrenceType": "WEEKLY",
  "weeklyDay": "TUESDAY",
  "startDate": "2026-02-24",
  "endDate": null,
  "startTime": "20:00:00",
  "subActivities": [
    { "name": "Soccer" },
    { "name": "Dinner" }
  ]
}
```

## Example generate occurrences

`POST /api/templates/1/occurrences/generate`

```json
{
  "from": "2026-02-24",
  "to": "2026-03-31"
}
```

## Example vote

`POST /api/public/occurrences/1/votes`

```json
{
  "subActivityId": 2,
  "participantName": "Lisandro",
  "participantEmail": "lisandro@example.com",
  "yesNo": true,
  "emailOptIn": true
}
```

## Email Reminders

The API sends automated reminder emails 24 hours before events to participants who:
- Voted **YES** on any sub-activity
- Provided an email address
- Set `emailOptIn: true` in their vote request

**Scheduled job**: Runs daily at 9 AM (configurable via `@Scheduled` cron in `EmailReminderService`).

**Configuration**:
- Set `app.mail.enabled=true` to enable email sending (default: `false`).
- Configure SMTP credentials via environment variables (`SMTP_USERNAME`, `SMTP_PASSWORD`, `MAIL_FROM`).

**Example for Gmail**:
1. Enable 2FA on your Google account.
2. Generate an [App Password](https://myaccount.google.com/apppasswords).
3. Set environment variables and restart the API.

## Notes

- Anyone can add/delete votes in MVP mode.
- Template edits can be added later; current implementation already snapshots sub-activities into each occurrence.
- Email reminders are opt-in only; voters without `emailOptIn: true` will not receive reminders.
