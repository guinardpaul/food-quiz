const fs = require('fs');
const path = require('path');

const apiUrl = process.env['API_URL'] ?? 'https://food-quiz-1une.onrender.com';

const content = `export const environment = {
  production: true,
  apiUrl: '${apiUrl}',
};
`;

const target = path.join(__dirname, '../src/environments/environment.prod.ts');
fs.writeFileSync(target, content, 'utf8');
console.log(`environment.prod.ts generated with apiUrl=${apiUrl}`);
