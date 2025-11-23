const barData = [
    { day: 'Seg', minutes: 30 },
    { day: 'Ter', minutes: 45 },
    { day: 'Qua', minutes: 50 },
    { day: 'Qui', minutes: 40 },
    { day: 'Sex', minutes: 55 },
    { day: 'Sáb', minutes: 60 },
    { day: 'Dom', minutes: 25 }
];

const lineData = [
    { day: 'Seg', uv: 5 },
    { day: 'Ter', uv: 6 },
    { day: 'Qua', uv: 7 },
    { day: 'Qui', uv: 8 },
    { day: 'Sex', uv: 9 },
    { day: 'Sáb', uv: 10 },
    { day: 'Dom', uv: 6 }
];

ReactDOM.render(
    React.createElement(Recharts.BarChart, { width: '100%', height: 200, data: barData },
        React.createElement(Recharts.XAxis, { dataKey: 'day', fontSize: 10 }),
        React.createElement(Recharts.YAxis, { fontSize: 10 }),
        React.createElement(Recharts.CartesianGrid, { strokeDasharray: '3 3', stroke: '#7c8d9c', strokeOpacity: 0.3 }),
        React.createElement(Recharts.Bar, { dataKey: 'minutes', fill: '#fdb813', radius: [4, 4, 0, 0] }),
        React.createElement(Recharts.Tooltip, { contentStyle: { backgroundColor: 'white', border: '1px solid #152e4c', fontSize: 12 } })
    ),
    document.getElementById('bar-chart')
);

ReactDOM.render(
    React.createElement(Recharts.LineChart, { width: '100%', height: 200, data: lineData },
        React.createElement(Recharts.XAxis, { dataKey: 'day', fontSize: 10 }),
        React.createElement(Recharts.YAxis, { domain: [0, 11], fontSize: 10 }),
        React.createElement(Recharts.CartesianGrid, { strokeDasharray: '3 3', stroke: '#7c8d9c', strokeOpacity: 0.3 }),
        React.createElement(Recharts.Line, { type: 'monotone', dataKey: 'uv', stroke: '#F5732F', strokeWidth: 2, dot: { fill: '#F5732F', r: 4 } }),
        React.createElement(Recharts.Tooltip, { contentStyle: { backgroundColor: 'white', border: '1px solid #152e4c', fontSize: 12 } })
    ),
    document.getElementById('line-chart')
);

const tabs = document.querySelectorAll('.tab');
const panels = document.querySelectorAll('.tab-panel');

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        panels.forEach(p => p.classList.remove('active'));
        tab.classList.add('active');
        document.getElementById(tab.dataset.tab).classList.add('active');
    });
});